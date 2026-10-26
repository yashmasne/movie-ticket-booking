/**
 * CinePass - Authentication & User State Management
 */
const Auth = {
    currentUser: null,

    init() {
        const stored = localStorage.getItem('cinepass_user');
        if (stored) {
            try {
                this.currentUser = JSON.parse(stored);
            } catch (e) {
                this.currentUser = null;
            }
        }
        this.updateNavUI();
        this.attachEventListeners();
    },

    getUser() {
        return this.currentUser;
    },

    isLoggedIn() {
        return !!this.currentUser;
    },

    isAdmin() {
        return this.currentUser && this.currentUser.role === 'ROLE_ADMIN';
    },

    setUser(user) {
        this.currentUser = user;
        if (user) {
            localStorage.setItem('cinepass_user', JSON.stringify(user));
        } else {
            localStorage.removeItem('cinepass_user');
        }
        this.updateNavUI();
    },

    logout() {
        this.setUser(null);
        showToast('Logged out successfully', 'success');
        if (window.App) {
            window.App.switchView('home');
        }
    },

    async login(email, password) {
        try {
            const res = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });
            const data = await res.json();
            if (data.success) {
                this.setUser(data.data);
                showToast(`Welcome back, ${data.data.fullName}!`, 'success');
                closeModal('authModal');
                return true;
            } else {
                showToast(data.message || 'Login failed', 'error');
                return false;
            }
        } catch (err) {
            showToast('Unable to connect to server', 'error');
            return false;
        }
    },

    async register(fullName, email, password, phone, role = 'ROLE_USER') {
        try {
            const res = await fetch('/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ fullName, email, password, phone, role })
            });
            const data = await res.json();
            if (data.success) {
                showToast('Registration successful! Please sign in.', 'success');
                // Switch to login tab
                document.getElementById('authTabLogin').click();
                document.getElementById('loginEmail').value = email;
                return true;
            } else {
                showToast(data.message || 'Registration failed', 'error');
                return false;
            }
        } catch (err) {
            showToast('Unable to connect to server', 'error');
            return false;
        }
    },

    quickLogin(type) {
        if (type === 'admin') {
            document.getElementById('loginEmail').value = 'admin@movieticket.com';
            document.getElementById('loginPassword').value = 'admin123';
        } else {
            document.getElementById('loginEmail').value = 'user@movieticket.com';
            document.getElementById('loginPassword').value = 'user123';
        }
        document.getElementById('loginForm').dispatchEvent(new Event('submit'));
    },

    updateNavUI() {
        const guestBlock = document.getElementById('navGuestBlock');
        const userBlock = document.getElementById('navUserBlock');
        const adminNavLink = document.getElementById('navAdminLink');
        const bookingsNavLink = document.getElementById('navBookingsLink');

        if (this.currentUser) {
            if (guestBlock) guestBlock.style.display = 'none';
            if (userBlock) userBlock.style.display = 'block';

            document.getElementById('navUserName').textContent = this.currentUser.fullName.split(' ')[0];
            document.getElementById('dropdownUserName').textContent = this.currentUser.fullName;
            document.getElementById('dropdownUserEmail').textContent = this.currentUser.email;
            document.getElementById('dropdownUserRole').textContent = this.isAdmin() ? 'ADMIN' : 'MEMBER';
            document.getElementById('userAvatarInitial').textContent = this.currentUser.fullName.charAt(0).toUpperCase();

            if (adminNavLink) {
                adminNavLink.style.display = this.isAdmin() ? 'inline-flex' : 'none';
            }
            if (bookingsNavLink) bookingsNavLink.style.display = 'inline-flex';
        } else {
            if (guestBlock) guestBlock.style.display = 'flex';
            if (userBlock) userBlock.style.display = 'none';
            if (adminNavLink) adminNavLink.style.display = 'none';
            if (bookingsNavLink) bookingsNavLink.style.display = 'none';
        }
    },

    attachEventListeners() {
        // Toggle user dropdown
        const userBtn = document.getElementById('userMenuBtn');
        const dropdown = document.getElementById('userDropdown');
        if (userBtn && dropdown) {
            userBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                dropdown.classList.toggle('show');
            });

            document.addEventListener('click', () => {
                dropdown.classList.remove('show');
            });
        }

        // Auth Tabs in Modal
        const tabLogin = document.getElementById('authTabLogin');
        const tabRegister = document.getElementById('authTabRegister');
        const loginPane = document.getElementById('loginFormPane');
        const registerPane = document.getElementById('registerFormPane');

        if (tabLogin && tabRegister) {
            tabLogin.addEventListener('click', () => {
                tabLogin.classList.add('active');
                tabRegister.classList.remove('active');
                loginPane.style.display = 'block';
                registerPane.style.display = 'none';
            });

            tabRegister.addEventListener('click', () => {
                tabRegister.classList.add('active');
                tabLogin.classList.remove('active');
                registerPane.style.display = 'block';
                loginPane.style.display = 'none';
            });
        }

        // Login form submit
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const email = document.getElementById('loginEmail').value.trim();
                const pass = document.getElementById('loginPassword').value;
                if (!email || !pass) {
                    showToast('Please enter both email and password', 'error');
                    return;
                }
                await Auth.login(email, pass);
            });
        }

        // Register form submit
        const registerForm = document.getElementById('registerForm');
        if (registerForm) {
            registerForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const name = document.getElementById('regName').value.trim();
                const email = document.getElementById('regEmail').value.trim();
                const pass = document.getElementById('regPassword').value;
                const phone = document.getElementById('regPhone').value.trim();
                const role = document.getElementById('regRole').value;

                if (!name || !email || !pass) {
                    showToast('Please fill all required fields', 'error');
                    return;
                }
                await Auth.register(name, email, pass, phone, role);
            });
        }
    }
};

window.Auth = Auth;

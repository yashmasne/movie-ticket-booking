/**
 * CinePass - Admin Portal Management (Movies, Cinemas, Showtimes, Bookings)
 */
const AdminModule = {
    currentTab: 'movies',
    allMovies: [],
    allCinemas: [],
    allScreens: [],

    async init() {
        await this.loadStats();
        this.switchAdminTab('movies');
        this.attachFormListeners();
    },

    async loadStats() {
        try {
            const res = await fetch('/api/admin/stats');
            const data = await res.json();
            if (data.success) {
                const s = data.data;
                document.getElementById('statTotalMovies').textContent = s.totalMovies;
                document.getElementById('statTotalCinemas').textContent = s.totalCinemas;
                document.getElementById('statTotalShowtimes').textContent = s.totalShowtimes;
                document.getElementById('statTotalBookings').textContent = s.totalBookings;
                document.getElementById('statTotalRevenue').textContent = `₹${Math.round(s.totalRevenue).toLocaleString()}`;
            }
        } catch (err) {
            console.error('Stats load error', err);
        }
    },

    switchAdminTab(tab) {
        this.currentTab = tab;
        document.querySelectorAll('.admin-tab-btn').forEach(btn => {
            btn.classList.toggle('active', btn.dataset.tab === tab);
        });

        document.getElementById('adminTabMovies').style.display = (tab === 'movies') ? 'block' : 'none';
        document.getElementById('adminTabCinemas').style.display = (tab === 'cinemas') ? 'block' : 'none';
        document.getElementById('adminTabShowtimes').style.display = (tab === 'showtimes') ? 'block' : 'none';
        document.getElementById('adminTabBookings').style.display = (tab === 'bookings') ? 'block' : 'none';

        if (tab === 'movies') this.loadMoviesTable();
        else if (tab === 'cinemas') this.loadCinemasTable();
        else if (tab === 'showtimes') this.loadShowtimesTable();
        else if (tab === 'bookings') this.loadBookingsTable();
    },

    // 1. Movies Table
    async loadMoviesTable() {
        const tbody = document.getElementById('adminMoviesTableBody');
        tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;padding:20px;">Loading movies...</td></tr>';
        try {
            const res = await fetch('/api/movies');
            const data = await res.json();
            this.allMovies = data.data || [];

            if (this.allMovies.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;padding:20px;">No movies found</td></tr>';
                return;
            }

            tbody.innerHTML = '';
            this.allMovies.forEach(m => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>
                        <img class="table-poster-thumb" src="${m.posterUrl}" alt="${m.title}">
                    </td>
                    <td>
                        <strong>${m.title}</strong>
                        <div style="font-size:11px;color:var(--text-dim);">${m.director || ''}</div>
                    </td>
                    <td>${m.genre}</td>
                    <td>${m.language} • ${m.durationMinutes}m</td>
                    <td>⭐ ${m.rating}</td>
                    <td>
                        <span class="badge ${m.status === 'NOW_SHOWING' ? 'badge-featured' : 'badge-format'}">
                            ${m.status === 'NOW_SHOWING' ? 'Now Showing' : 'Coming Soon'}
                        </span>
                    </td>
                    <td>
                        <div style="display:flex;gap:6px;">
                            <button class="btn btn-secondary btn-sm" onclick="AdminModule.openEditMovieModal(${m.id})">Edit</button>
                            <button class="btn btn-danger btn-sm" onclick="AdminModule.deleteMovie(${m.id})">Delete</button>
                        </div>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } catch (err) {
            tbody.innerHTML = '<tr><td colspan="7" style="color:#ef4444;text-align:center;">Failed to load movies</td></tr>';
        }
    },

    openAddMovieModal() {
        document.getElementById('movieModalTitle').textContent = 'Add New Movie';
        document.getElementById('movieForm').reset();
        document.getElementById('movieIdInput').value = '';
        openModal('adminMovieModal');
    },

    openEditMovieModal(movieId) {
        const movie = this.allMovies.find(m => m.id === movieId);
        if (!movie) return;

        document.getElementById('movieModalTitle').textContent = 'Edit Movie';
        document.getElementById('movieIdInput').value = movie.id;
        document.getElementById('movieTitleInput').value = movie.title || '';
        document.getElementById('movieDescInput').value = movie.description || '';
        document.getElementById('movieGenreInput').value = movie.genre || '';
        document.getElementById('movieLanguageInput').value = movie.language || 'English';
        document.getElementById('movieDurationInput').value = movie.durationMinutes || 120;
        document.getElementById('movieRatingInput').value = movie.rating || 8.0;
        document.getElementById('movieReleaseDateInput').value = movie.releaseDate || '';
        document.getElementById('moviePosterUrlInput').value = movie.posterUrl || '';
        document.getElementById('movieBannerUrlInput').value = movie.bannerUrl || '';
        document.getElementById('movieTrailerUrlInput').value = movie.trailerUrl || '';
        document.getElementById('movieDirectorInput').value = movie.director || '';
        document.getElementById('movieCastInput').value = movie.castMembers || '';
        document.getElementById('movieAgeRatingInput').value = movie.ageRating || 'UA 16+';
        document.getElementById('movieStatusInput').value = movie.status || 'NOW_SHOWING';

        openModal('adminMovieModal');
    },

    async saveMovie(event) {
        event.preventDefault();
        const id = document.getElementById('movieIdInput').value;
        const movieData = {
            title: document.getElementById('movieTitleInput').value.trim(),
            description: document.getElementById('movieDescInput').value.trim(),
            genre: document.getElementById('movieGenreInput').value.trim(),
            language: document.getElementById('movieLanguageInput').value.trim(),
            durationMinutes: parseInt(document.getElementById('movieDurationInput').value, 10),
            rating: parseFloat(document.getElementById('movieRatingInput').value),
            releaseDate: document.getElementById('movieReleaseDateInput').value || null,
            posterUrl: document.getElementById('moviePosterUrlInput').value.trim(),
            bannerUrl: document.getElementById('movieBannerUrlInput').value.trim(),
            trailerUrl: document.getElementById('movieTrailerUrlInput').value.trim(),
            director: document.getElementById('movieDirectorInput').value.trim(),
            castMembers: document.getElementById('movieCastInput').value.trim(),
            ageRating: document.getElementById('movieAgeRatingInput').value,
            status: document.getElementById('movieStatusInput').value
        };

        const url = id ? `/api/movies/${id}` : '/api/movies';
        const method = id ? 'PUT' : 'POST';

        try {
            const res = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(movieData)
            });
            const data = await res.json();
            if (data.success) {
                showToast(`Movie ${id ? 'updated' : 'created'} successfully!`, 'success');
                closeModal('adminMovieModal');
                this.loadMoviesTable();
                this.loadStats();
                if (window.App) window.App.loadMovies();
            } else {
                showToast(data.message || 'Operation failed', 'error');
            }
        } catch (err) {
            showToast('Failed to save movie', 'error');
        }
    },

    async deleteMovie(id) {
        if (!confirm('Are you sure you want to delete this movie? This will also remove associated showtimes.')) {
            return;
        }

        try {
            const res = await fetch(`/api/movies/${id}`, { method: 'DELETE' });
            const data = await res.json();
            if (data.success) {
                showToast('Movie deleted successfully', 'success');
                this.loadMoviesTable();
                this.loadStats();
                if (window.App) window.App.loadMovies();
            } else {
                showToast(data.message || 'Failed to delete movie', 'error');
            }
        } catch (err) {
            showToast('Error deleting movie', 'error');
        }
    },

    // 2. Cinemas Table
    async loadCinemasTable() {
        const tbody = document.getElementById('adminCinemasTableBody');
        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:20px;">Loading cinemas...</td></tr>';
        try {
            const res = await fetch('/api/cinemas');
            const data = await res.json();
            this.allCinemas = data.data || [];

            if (this.allCinemas.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:20px;">No cinemas found</td></tr>';
                return;
            }

            tbody.innerHTML = '';
            this.allCinemas.forEach(c => {
                const tr = document.createElement('tr');
                const screenCount = c.screens ? c.screens.length : 0;
                tr.innerHTML = `
                    <td><strong>${c.name}</strong></td>
                    <td>${c.city}</td>
                    <td>${c.address || '-'}</td>
                    <td><span class="badge badge-format">${screenCount} Screens</span></td>
                    <td>
                        <button class="btn btn-secondary btn-sm" onclick="AdminModule.openAddScreenModal(${c.id}, '${c.name.replace(/'/g, "\\'")}')">+ Add Screen</button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } catch (err) {
            tbody.innerHTML = '<tr><td colspan="5" style="color:#ef4444;text-align:center;">Failed to load cinemas</td></tr>';
        }
    },

    openAddCinemaModal() {
        document.getElementById('cinemaForm').reset();
        openModal('adminCinemaModal');
    },

    async saveCinema(event) {
        event.preventDefault();
        const cinema = {
            name: document.getElementById('cinemaNameInput').value.trim(),
            city: document.getElementById('cinemaCityInput').value.trim(),
            address: document.getElementById('cinemaAddressInput').value.trim(),
            phone: document.getElementById('cinemaPhoneInput').value.trim(),
            amenities: document.getElementById('cinemaAmenitiesInput').value.trim()
        };

        try {
            const res = await fetch('/api/cinemas', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(cinema)
            });
            const data = await res.json();
            if (data.success) {
                showToast('Cinema added successfully!', 'success');
                closeModal('adminCinemaModal');
                this.loadCinemasTable();
                this.loadStats();
                if (window.App) window.App.loadCities();
            } else {
                showToast(data.message || 'Failed to add cinema', 'error');
            }
        } catch (err) {
            showToast('Error creating cinema', 'error');
        }
    },

    openAddScreenModal(cinemaId, cinemaName) {
        document.getElementById('screenCinemaIdInput').value = cinemaId;
        document.getElementById('screenCinemaNameDisplay').textContent = cinemaName;
        document.getElementById('screenForm').reset();
        openModal('adminScreenModal');
    },

    async saveScreen(event) {
        event.preventDefault();
        const cinemaId = document.getElementById('screenCinemaIdInput').value;
        const screen = {
            name: document.getElementById('screenNameInput').value.trim(),
            screenType: document.getElementById('screenTypeInput').value,
            totalRows: parseInt(document.getElementById('screenRowsInput').value, 10),
            totalCols: parseInt(document.getElementById('screenColsInput').value, 10)
        };

        try {
            const res = await fetch(`/api/cinemas/${cinemaId}/screens`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(screen)
            });
            const data = await res.json();
            if (data.success) {
                showToast('Screen created with automated seat layout!', 'success');
                closeModal('adminScreenModal');
                this.loadCinemasTable();
            } else {
                showToast(data.message || 'Failed to create screen', 'error');
            }
        } catch (err) {
            showToast('Error saving screen', 'error');
        }
    },

    // 3. Showtimes Table
    async loadShowtimesTable() {
        const tbody = document.getElementById('adminShowtimesTableBody');
        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;padding:20px;">Loading showtimes...</td></tr>';
        try {
            const res = await fetch('/api/showtimes');
            const data = await res.json();
            const showtimes = data.data || [];

            if (showtimes.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;padding:20px;">No showtimes scheduled</td></tr>';
                return;
            }

            tbody.innerHTML = '';
            showtimes.forEach(s => {
                const tr = document.createElement('tr');
                const movie = s.movie;
                const screen = s.screen;
                const cinema = screen.cinema;

                tr.innerHTML = `
                    <td><strong>${movie.title}</strong></td>
                    <td>${cinema ? cinema.name : 'Cinema'} (${screen.name})</td>
                    <td>${s.showDate}</td>
                    <td>${BookingModule.formatTime(s.startTime)}</td>
                    <td><strong>₹${s.basePrice}</strong></td>
                    <td>
                        <button class="btn btn-danger btn-sm" onclick="AdminModule.deleteShowtime(${s.id})">Delete</button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } catch (err) {
            tbody.innerHTML = '<tr><td colspan="6" style="color:#ef4444;text-align:center;">Failed to load showtimes</td></tr>';
        }
    },

    async openAddShowtimeModal() {
        // Populate Movies dropdown
        const movieSelect = document.getElementById('showtimeMovieSelect');
        movieSelect.innerHTML = '<option value="">-- Select Movie --</option>';
        if (this.allMovies.length === 0) {
            const mRes = await fetch('/api/movies');
            const mData = await mRes.json();
            this.allMovies = mData.data || [];
        }
        this.allMovies.forEach(m => {
            const opt = document.createElement('option');
            opt.value = m.id;
            opt.textContent = m.title;
            movieSelect.appendChild(opt);
        });

        // Populate Screens dropdown
        const screenSelect = document.getElementById('showtimeScreenSelect');
        screenSelect.innerHTML = '<option value="">-- Select Screen --</option>';
        if (this.allCinemas.length === 0) {
            const cRes = await fetch('/api/cinemas');
            const cData = await cRes.json();
            this.allCinemas = cData.data || [];
        }
        this.allCinemas.forEach(c => {
            if (c.screens) {
                c.screens.forEach(s => {
                    const opt = document.createElement('option');
                    opt.value = s.id;
                    opt.textContent = `${c.name} - ${s.name} (${s.screenType})`;
                    screenSelect.appendChild(opt);
                });
            }
        });

        // Set default date to today
        document.getElementById('showtimeDateInput').value = new Date().toISOString().split('T')[0];
        openModal('adminShowtimeModal');
    },

    async saveShowtime(event) {
        event.preventDefault();
        const req = {
            movieId: parseInt(document.getElementById('showtimeMovieSelect').value, 10),
            screenId: parseInt(document.getElementById('showtimeScreenSelect').value, 10),
            showDate: document.getElementById('showtimeDateInput').value,
            startTime: document.getElementById('showtimeTimeInput').value + ':00',
            basePrice: parseFloat(document.getElementById('showtimePriceInput').value)
        };

        try {
            const res = await fetch('/api/showtimes', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(req)
            });
            const data = await res.json();
            if (data.success) {
                showToast('Showtime scheduled successfully!', 'success');
                closeModal('adminShowtimeModal');
                this.loadShowtimesTable();
                this.loadStats();
            } else {
                showToast(data.message || 'Failed to create showtime', 'error');
            }
        } catch (err) {
            showToast('Error scheduling showtime', 'error');
        }
    },

    async deleteShowtime(id) {
        if (!confirm('Are you sure you want to delete this showtime?')) return;
        try {
            const res = await fetch(`/api/showtimes/${id}`, { method: 'DELETE' });
            const data = await res.json();
            if (data.success) {
                showToast('Showtime deleted', 'success');
                this.loadShowtimesTable();
                this.loadStats();
            } else {
                showToast(data.message || 'Failed to delete', 'error');
            }
        } catch (err) {
            showToast('Error deleting showtime', 'error');
        }
    },

    // 4. Bookings Table
    async loadBookingsTable() {
        const tbody = document.getElementById('adminBookingsTableBody');
        tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;padding:20px;">Loading bookings...</td></tr>';
        try {
            const res = await fetch('/api/admin/bookings');
            const data = await res.json();
            const bookings = data.data || [];

            if (bookings.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;padding:20px;">No bookings found</td></tr>';
                return;
            }

            tbody.innerHTML = '';
            bookings.forEach(b => {
                const tr = document.createElement('tr');
                const isConfirmed = b.status === 'CONFIRMED';
                const seatNames = b.bookingSeats ? b.bookingSeats.map(bs => bs.seat.seatCode).join(', ') : '-';

                tr.innerHTML = `
                    <td><strong>#${b.bookingReference}</strong></td>
                    <td>
                        <div>${b.customerName}</div>
                        <div style="font-size:11px;color:var(--text-dim);">${b.customerEmail}</div>
                    </td>
                    <td>${b.showtime.movie.title}</td>
                    <td>${b.showtime.showDate} ${BookingModule.formatTime(b.showtime.startTime)}</td>
                    <td><span class="badge badge-format">${seatNames}</span></td>
                    <td><strong style="color:var(--accent-gold);">₹${b.totalAmount}</strong></td>
                    <td>
                        <span class="status-badge ${isConfirmed ? 'status-confirmed' : 'status-cancelled'}">
                            ${b.status}
                        </span>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } catch (err) {
            tbody.innerHTML = '<tr><td colspan="7" style="color:#ef4444;text-align:center;">Failed to load bookings</td></tr>';
        }
    },

    attachFormListeners() {
        const movieForm = document.getElementById('movieForm');
        if (movieForm) movieForm.addEventListener('submit', (e) => this.saveMovie(e));

        const cinemaForm = document.getElementById('cinemaForm');
        if (cinemaForm) cinemaForm.addEventListener('submit', (e) => this.saveCinema(e));

        const screenForm = document.getElementById('screenForm');
        if (screenForm) screenForm.addEventListener('submit', (e) => this.saveScreen(e));

        const showtimeForm = document.getElementById('showtimeForm');
        if (showtimeForm) showtimeForm.addEventListener('submit', (e) => this.saveShowtime(e));
    }
};

window.AdminModule = AdminModule;

/**
 * CinePass - Master Application Controller
 */
const App = {
    movies: [],
    cities: [],
    currentView: 'home',
    selectedGenre: '',
    selectedLanguage: '',
    selectedStatus: 'NOW_SHOWING',
    searchQuery: '',
    selectedCity: 'All Cities',
    selectedMovieId: null,
    selectedDate: null,

    async init() {
        Auth.init();
        await this.loadCities();
        await this.loadMovies();
        this.setupEventListeners();
    },

    switchView(viewName) {
        this.currentView = viewName;
        document.getElementById('homeView').style.display = (viewName === 'home') ? 'block' : 'none';
        document.getElementById('bookingsView').style.display = (viewName === 'bookings') ? 'block' : 'none';
        document.getElementById('adminView').style.display = (viewName === 'admin') ? 'block' : 'none';

        // Update Nav links active state
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.toggle('active', item.dataset.nav === viewName);
        });

        window.scrollTo({ top: 0, behavior: 'smooth' });

        if (viewName === 'bookings') {
            BookingModule.loadUserBookings();
        } else if (viewName === 'admin') {
            AdminModule.init();
        }
    },

    async loadCities() {
        try {
            const res = await fetch('/api/cinemas/cities');
            const data = await res.json();
            this.cities = data.data || [];
            const select = document.getElementById('citySelect');
            select.innerHTML = '<option value="All Cities">📍 All Cities</option>';
            this.cities.forEach(city => {
                const opt = document.createElement('option');
                opt.value = city;
                opt.textContent = `📍 ${city}`;
                select.appendChild(opt);
            });
        } catch (err) {
            console.error('Cities load error', err);
        }
    },

    async loadMovies() {
        try {
            const params = new URLSearchParams();
            if (this.searchQuery) params.append('search', this.searchQuery);
            if (this.selectedGenre) params.append('genre', this.selectedGenre);
            if (this.selectedLanguage) params.append('language', this.selectedLanguage);
            if (this.selectedStatus) params.append('status', this.selectedStatus);

            const res = await fetch(`/api/movies?${params.toString()}`);
            const data = await res.json();
            this.movies = data.data || [];

            this.renderHeroBanner();
            this.renderMoviesGrid();
        } catch (err) {
            console.error('Movies fetch error', err);
            showToast('Failed to load movies', 'error');
        }
    },

    renderHeroBanner() {
        if (!this.movies || this.movies.length === 0) return;
        const heroMovie = this.movies[0];

        document.getElementById('heroBackdrop').src = heroMovie.bannerUrl || heroMovie.posterUrl;
        document.getElementById('heroRating').textContent = heroMovie.rating;
        document.getElementById('heroAgeRating').textContent = heroMovie.ageRating || 'UA 16+';
        document.getElementById('heroTitle').textContent = heroMovie.title;
        document.getElementById('heroGenre').textContent = heroMovie.genre;
        document.getElementById('heroDuration').textContent = `${heroMovie.durationMinutes}m`;
        document.getElementById('heroLanguage').textContent = heroMovie.language;
        document.getElementById('heroDesc').textContent = heroMovie.description;

        document.getElementById('heroBookBtn').onclick = () => this.openMovieDetails(heroMovie.id);
        document.getElementById('heroTrailerBtn').onclick = () => this.openTrailerModal(heroMovie.trailerUrl, heroMovie.title);
    },

    renderMoviesGrid() {
        const grid = document.getElementById('moviesGrid');
        const countDisplay = document.getElementById('moviesCountDisplay');
        countDisplay.textContent = `(${this.movies.length} Movies)`;

        if (this.movies.length === 0) {
            grid.innerHTML = `
                <div class="empty-state" style="grid-column: 1 / -1;">
                    <div class="empty-state-icon">🎬</div>
                    <h3>No Movies Found</h3>
                    <p>Try adjusting your search criteria or filters.</p>
                </div>`;
            return;
        }

        grid.innerHTML = '';
        this.movies.forEach(movie => {
            const card = document.createElement('div');
            card.className = 'movie-card';
            card.innerHTML = `
                <div class="poster-wrapper">
                    <img class="movie-poster" src="${movie.posterUrl}" alt="${movie.title}" loading="lazy">
                    <div class="poster-gradient"></div>
                    <div class="card-rating-badge">⭐ ${movie.rating}</div>
                    <div class="card-format-tag">${movie.language}</div>
                </div>
                <div class="movie-info">
                    <h3 class="movie-title">${movie.title}</h3>
                    <p class="movie-tags">${movie.genre}</p>
                    <div class="movie-meta-bar">
                        <span>⏳ ${movie.durationMinutes} mins</span>
                        <span>${movie.ageRating || 'UA'}</span>
                    </div>
                    <div class="card-actions">
                        <button class="btn btn-primary btn-sm" onclick="event.stopPropagation(); App.openMovieDetails(${movie.id})">Book Tickets</button>
                        <button class="btn btn-secondary btn-sm" onclick="event.stopPropagation(); App.openMovieDetails(${movie.id})">Details</button>
                    </div>
                </div>
            `;
            card.addEventListener('click', () => this.openMovieDetails(movie.id));
            grid.appendChild(card);
        });
    },

    async openMovieDetails(movieId) {
        this.selectedMovieId = movieId;
        try {
            showToast('Loading movie details...', 'info');
            const res = await fetch(`/api/movies/${movieId}`);
            const data = await res.json();
            if (!data.success) return;
            const movie = data.data;

            document.getElementById('detailsBackdrop').src = movie.bannerUrl || movie.posterUrl;
            document.getElementById('detailsPoster').src = movie.posterUrl;
            document.getElementById('detailsTitle').textContent = movie.title;
            document.getElementById('detailsRating').textContent = movie.rating;
            document.getElementById('detailsVotes').textContent = `(${movie.votes.toLocaleString()} votes)`;
            document.getElementById('detailsDuration').textContent = `${movie.durationMinutes} mins`;
            document.getElementById('detailsLanguage').textContent = movie.language;
            document.getElementById('detailsGenre').textContent = movie.genre;
            document.getElementById('detailsAgeRating').textContent = movie.ageRating || 'UA 16+';
            document.getElementById('detailsDescription').textContent = movie.description;
            document.getElementById('detailsDirector').textContent = movie.director || 'N/A';
            document.getElementById('detailsCast').textContent = movie.castMembers || 'N/A';

            document.getElementById('detailsTrailerBtn').onclick = () => this.openTrailerModal(movie.trailerUrl, movie.title);

            // Fetch available dates
            await this.loadShowDates(movieId);

            openModal('movieDetailsModal');
        } catch (err) {
            console.error(err);
            showToast('Failed to load movie details', 'error');
        }
    },

    async loadShowDates(movieId) {
        const bar = document.getElementById('detailsDatePickerBar');
        bar.innerHTML = '<span style="color:var(--text-dim);font-size:13px;">Checking available dates...</span>';

        try {
            const res = await fetch(`/api/movies/${movieId}/dates`);
            const data = await res.json();
            const dates = data.data || [];

            bar.innerHTML = '';
            if (dates.length === 0) {
                // Generate next 5 days fallback
                const today = new Date();
                for (let i = 0; i < 5; i++) {
                    const d = new Date();
                    d.setDate(today.getDate() + i);
                    dates.push(d.toISOString().split('T')[0]);
                }
            }

            dates.forEach((dateStr, idx) => {
                const dateObj = new Date(dateStr + 'T00:00:00');
                const dayName = (idx === 0) ? 'TODAY' : (idx === 1 ? 'TOMORROW' : dateObj.toLocaleDateString('en-US', { weekday: 'short' }));
                const dayNum = dateObj.getDate();
                const monthName = dateObj.toLocaleDateString('en-US', { month: 'short' });

                const pill = document.createElement('div');
                pill.className = `date-pill ${idx === 0 ? 'active' : ''}`;
                pill.innerHTML = `
                    <div class="date-day">${dayName}</div>
                    <div class="date-num">${dayNum}</div>
                    <div style="font-size: 10px; text-transform: uppercase;">${monthName}</div>
                `;
                pill.addEventListener('click', () => {
                    document.querySelectorAll('.date-pill').forEach(p => p.classList.remove('active'));
                    pill.classList.add('active');
                    this.selectedDate = dateStr;
                    this.loadCinemasAndShowtimes(movieId, dateStr);
                });
                bar.appendChild(pill);
            });

            this.selectedDate = dates[0];
            await this.loadCinemasAndShowtimes(movieId, dates[0]);
        } catch (err) {
            bar.innerHTML = '<span style="color:#ef4444;font-size:13px;">Unable to load dates</span>';
        }
    },

    async loadCinemasAndShowtimes(movieId, dateStr) {
        const container = document.getElementById('cinemaShowtimesContainer');
        container.innerHTML = '<div style="text-align:center;padding:24px;color:var(--text-muted);">Finding cinemas & showtimes...</div>';

        try {
            const cityParam = (this.selectedCity && this.selectedCity !== 'All Cities') ? `&city=${encodeURIComponent(this.selectedCity)}` : '';
            const res = await fetch(`/api/showtimes?movieId=${movieId}&date=${dateStr}${cityParam}`);
            const data = await res.json();
            const showtimes = data.data || [];

            if (showtimes.length === 0) {
                container.innerHTML = `
                    <div class="empty-state" style="padding:30px;">
                        <div class="empty-state-icon">🍿</div>
                        <h4>No Showtimes Available</h4>
                        <p style="font-size:13px;">Try selecting another date or city filter.</p>
                    </div>`;
                return;
            }

            // Group showtimes by Cinema
            const cinemaMap = new Map();
            showtimes.forEach(st => {
                const screen = st.screen;
                const cinema = screen.cinema;
                const cinemaId = cinema ? cinema.id : 0;
                if (!cinemaMap.has(cinemaId)) {
                    cinemaMap.set(cinemaId, {
                        cinema: cinema,
                        showtimes: []
                    });
                }
                cinemaMap.get(cinemaId).showtimes.push(st);
            });

            container.innerHTML = '';
            cinemaMap.forEach(({ cinema, showtimes }) => {
                const card = document.createElement('div');
                card.className = 'cinema-showtime-card';

                const cName = cinema ? cinema.name : 'Standard Cinema';
                const cAddress = cinema ? cinema.address : 'Downtown Center';
                const cAmenities = cinema ? cinema.amenities : '';

                let slotsHtml = '';
                showtimes.forEach(st => {
                    slotsHtml += `
                        <button class="time-slot-btn" onclick="BookingModule.openSeatSelection(${st.id})">
                            <span class="slot-time">${BookingModule.formatTime(st.startTime)}</span>
                            <span class="slot-format">${st.screen.screenType}</span>
                            <span class="slot-price">from ₹${st.basePrice}</span>
                        </button>
                    `;
                });

                card.innerHTML = `
                    <div class="cinema-header">
                        <div>
                            <h4 class="cinema-title">${cName}</h4>
                            <div class="cinema-location">${cAddress}</div>
                            ${cAmenities ? `<div style="font-size:11px;color:var(--text-dim);margin-top:2px;">✨ ${cAmenities}</div>` : ''}
                        </div>
                    </div>
                    <div class="showtime-buttons">
                        ${slotsHtml}
                    </div>
                `;
                container.appendChild(card);
            });
        } catch (err) {
            container.innerHTML = '<div style="color:#ef4444;text-align:center;padding:20px;">Failed to load showtimes</div>';
        }
    },

    openTrailerModal(trailerUrl, movieTitle) {
        let embedUrl = '';
        if (trailerUrl && trailerUrl.includes('youtube.com/watch?v=')) {
            const videoId = trailerUrl.split('watch?v=')[1].split('&')[0];
            embedUrl = `https://www.youtube.com/embed/${videoId}?autoplay=1`;
        } else if (trailerUrl && trailerUrl.includes('youtu.be/')) {
            const videoId = trailerUrl.split('youtu.be/')[1].split('?')[0];
            embedUrl = `https://www.youtube.com/embed/${videoId}?autoplay=1`;
        } else {
            embedUrl = 'https://www.youtube.com/embed/Way9Dexny3w?autoplay=1';
        }

        document.getElementById('trailerModalTitle').textContent = `${movieTitle} - Official Trailer`;
        document.getElementById('trailerIframe').src = embedUrl;
        openModal('trailerModal');
    },

    setupEventListeners() {
        // City Select change
        const citySelect = document.getElementById('citySelect');
        citySelect.addEventListener('change', (e) => {
            this.selectedCity = e.target.value;
            if (this.selectedMovieId) {
                this.loadCinemasAndShowtimes(this.selectedMovieId, this.selectedDate);
            }
        });

        // Search input debounce
        let searchTimer = null;
        const searchInput = document.getElementById('searchInput');
        searchInput.addEventListener('input', (e) => {
            clearTimeout(searchTimer);
            searchTimer = setTimeout(() => {
                this.searchQuery = e.target.value;
                this.loadMovies();
            }, 300);
        });

        // Language filter
        const langSelect = document.getElementById('langSelect');
        langSelect.addEventListener('change', (e) => {
            this.selectedLanguage = e.target.value;
            this.loadMovies();
        });

        // Status Tabs (Now Showing / Coming Soon)
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
                this.selectedStatus = btn.dataset.status;
                this.loadMovies();
            });
        });

        // Genre Pills
        document.querySelectorAll('.genre-pill').forEach(pill => {
            pill.addEventListener('click', (e) => {
                document.querySelectorAll('.genre-pill').forEach(p => p.classList.remove('active'));
                pill.classList.add('active');
                this.selectedGenre = pill.dataset.genre || '';
                this.loadMovies();
            });
        });

        // Payment input live card preview
        const cardNumInput = document.getElementById('cardNumInput');
        if (cardNumInput) {
            cardNumInput.addEventListener('input', (e) => {
                let v = e.target.value.replace(/\D/g, '').substring(0, 16);
                let formatted = v.match(/.{1,4}/g)?.join(' ') || v;
                e.target.value = formatted;
                document.getElementById('cardPreviewNumber').textContent = formatted || '•••• •••• •••• ••••';
            });
        }

        const cardHolderInput = document.getElementById('cardHolderInput');
        if (cardHolderInput) {
            cardHolderInput.addEventListener('input', (e) => {
                document.getElementById('cardPreviewHolder').textContent = e.target.value.toUpperCase() || 'YOUR NAME';
            });
        }

        const cardExpiryInput = document.getElementById('cardExpiryInput');
        if (cardExpiryInput) {
            cardExpiryInput.addEventListener('input', (e) => {
                let v = e.target.value.replace(/\D/g, '').substring(0, 4);
                if (v.length >= 2) v = v.substring(0, 2) + '/' + v.substring(2);
                e.target.value = v;
                document.getElementById('cardPreviewExpiry').textContent = v || 'MM/YY';
            });
        }

        // Trailer modal close cleanup
        document.getElementById('trailerModalClose').addEventListener('click', () => {
            document.getElementById('trailerIframe').src = '';
        });
    }
};

// Global UI Modal Functions
function openModal(id) {
    const el = document.getElementById(id);
    if (el) el.classList.add('open');
}

function closeModal(id) {
    const el = document.getElementById(id);
    if (el) {
        el.classList.remove('open');
        if (id === 'trailerModal') {
            const iframe = document.getElementById('trailerIframe');
            if (iframe) iframe.src = '';
        }
    }
}

// Global Toast System
function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    const icon = type === 'success' ? '✅' : (type === 'error' ? '❌' : 'ℹ️');
    toast.innerHTML = `<span>${icon}</span> <span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(40px)';
        toast.style.transition = 'all 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

// Bootstrap on window load
document.addEventListener('DOMContentLoaded', () => {
    App.init();
});

window.App = App;
window.openModal = openModal;
window.closeModal = closeModal;
window.showToast = showToast;

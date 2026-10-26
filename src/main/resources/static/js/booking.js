/**
 * CinePass - Seat Selection, Payment Simulation & Ticket Confirmation
 */
const BookingModule = {
    currentShowtime: null,
    seatLayout: null,
    selectedSeats: new Map(), // seatId -> seatObj
    activePaymentMethod: 'CREDIT_CARD',

    async openSeatSelection(showtimeId) {
        try {
            showToast('Loading cinema layout...', 'info');
            const res = await fetch(`/api/showtimes/${showtimeId}/seats`);
            const data = await res.json();
            if (!data.success) {
                showToast(data.message || 'Unable to load seats', 'error');
                return;
            }

            this.seatLayout = data.data;
            this.currentShowtime = data.data.showtime;
            this.selectedSeats.clear();

            this.renderSeatHeader();
            this.renderSeatGrid();
            this.updateSummaryBar();

            closeModal('movieDetailsModal');
            openModal('seatModal');
        } catch (err) {
            console.error(err);
            showToast('Failed to load seat layout', 'error');
        }
    },

    renderSeatHeader() {
        const movie = this.currentShowtime.movie;
        const screen = this.currentShowtime.screen;
        const cinema = screen.cinema;

        document.getElementById('seatMovieTitle').textContent = movie.title;
        document.getElementById('seatCinemaInfo').textContent = 
            `${cinema ? cinema.name : 'Cinema'}, ${screen.name} (${screen.screenType})`;
        document.getElementById('seatDateTimeInfo').textContent = 
            `${this.currentShowtime.showDate} • ${this.formatTime(this.currentShowtime.startTime)}`;
    },

    renderSeatGrid() {
        const container = document.getElementById('seatGridContainer');
        container.innerHTML = '';

        // Group seats by row
        const rowMap = new Map();
        for (const seat of this.seatLayout.seats) {
            if (!rowMap.has(seat.seatRow)) {
                rowMap.set(seat.seatRow, []);
            }
            rowMap.get(seat.seatRow).push(seat);
        }

        // Render each row
        rowMap.forEach((seats, rowLabel) => {
            const rowEl = document.createElement('div');
            rowEl.className = 'seat-row';

            const labelEl = document.createElement('div');
            labelEl.className = 'seat-row-label';
            labelEl.textContent = rowLabel;
            rowEl.appendChild(labelEl);

            seats.forEach(seat => {
                const seatEl = document.createElement('div');
                seatEl.className = `seat ${seat.tier.toLowerCase()}`;
                if (seat.booked) {
                    seatEl.classList.add('booked');
                }
                seatEl.textContent = seat.seatNumber;
                seatEl.dataset.id = seat.id;
                seatEl.dataset.code = seat.seatCode;
                seatEl.dataset.tier = seat.tier;
                seatEl.dataset.price = seat.calculatedPrice;

                // Tooltip
                const tooltip = document.createElement('div');
                tooltip.className = 'seat-tooltip';
                tooltip.textContent = seat.booked ? `${seat.seatCode} (Booked)` : `${seat.seatCode} • ${seat.tier} • ₹${seat.calculatedPrice}`;
                seatEl.appendChild(tooltip);

                if (!seat.booked) {
                    seatEl.addEventListener('click', () => this.toggleSeatSelection(seat, seatEl));
                }

                rowEl.appendChild(seatEl);
            });

            container.appendChild(rowEl);
        });
    },

    toggleSeatSelection(seat, element) {
        if (this.selectedSeats.has(seat.id)) {
            this.selectedSeats.delete(seat.id);
            element.classList.remove('selected');
        } else {
            if (this.selectedSeats.size >= 8) {
                showToast('Maximum 8 tickets per transaction', 'info');
                return;
            }
            this.selectedSeats.set(seat.id, seat);
            element.classList.add('selected');
        }
        this.updateSummaryBar();
    },

    updateSummaryBar() {
        const chipsContainer = document.getElementById('selectedSeatChips');
        chipsContainer.innerHTML = '';

        if (this.selectedSeats.size === 0) {
            chipsContainer.innerHTML = '<span style="font-size: 13px; color: var(--text-dim);">No seats selected</span>';
        } else {
            this.selectedSeats.forEach(seat => {
                const chip = document.createElement('span');
                chip.className = 'seat-chip';
                chip.textContent = `${seat.seatCode} (₹${seat.calculatedPrice})`;
                chipsContainer.appendChild(chip);
            });
        }

        let subtotal = 0;
        this.selectedSeats.forEach(s => subtotal += s.calculatedPrice);
        const taxes = Math.round(subtotal * 0.18 * 100) / 100;
        const total = Math.round((subtotal + taxes) * 100) / 100;

        document.getElementById('seatSubtotalDisplay').textContent = `₹${subtotal} + ₹${taxes} GST`;
        document.getElementById('seatTotalDisplay').textContent = `₹${total}`;

        const bookBtn = document.getElementById('proceedToPaymentBtn');
        bookBtn.disabled = this.selectedSeats.size === 0;
        bookBtn.style.opacity = this.selectedSeats.size === 0 ? '0.5' : '1';
    },

    proceedToPayment() {
        if (this.selectedSeats.size === 0) {
            showToast('Please select at least one seat', 'info');
            return;
        }

        // Prefill user details if logged in
        const user = Auth.getUser();
        if (user) {
            document.getElementById('payCustomerName').value = user.fullName;
            document.getElementById('payCustomerEmail').value = user.email;
            document.getElementById('payCustomerPhone').value = user.phone || '';
        }

        let subtotal = 0;
        this.selectedSeats.forEach(s => subtotal += s.calculatedPrice);
        const taxes = Math.round(subtotal * 0.18 * 100) / 100;
        const total = Math.round((subtotal + taxes) * 100) / 100;

        document.getElementById('payModalMovieTitle').textContent = this.currentShowtime.movie.title;
        document.getElementById('payModalSeats').textContent = Array.from(this.selectedSeats.values()).map(s => s.seatCode).join(', ');
        document.getElementById('payModalTotal').textContent = `₹${total}`;
        document.getElementById('paySubmitBtnText').textContent = `Pay ₹${total}`;

        closeModal('seatModal');
        openModal('paymentModal');
        this.setPaymentMethod('CREDIT_CARD');
    },

    setPaymentMethod(method) {
        this.activePaymentMethod = method;
        document.querySelectorAll('.pay-tab-btn').forEach(btn => {
            btn.classList.toggle('active', btn.dataset.method === method);
        });

        document.getElementById('cardPaymentPane').style.display = (method === 'CREDIT_CARD' || method === 'DEBIT_CARD') ? 'block' : 'none';
        document.getElementById('upiPaymentPane').style.display = (method === 'UPI') ? 'block' : 'none';
        document.getElementById('netBankingPaymentPane').style.display = (method === 'NET_BANKING') ? 'block' : 'none';
    },

    async processPaymentAndBooking(event) {
        event.preventDefault();

        const name = document.getElementById('payCustomerName').value.trim();
        const email = document.getElementById('payCustomerEmail').value.trim();
        const phone = document.getElementById('payCustomerPhone').value.trim();

        if (!name || !email) {
            showToast('Please provide your name and email', 'error');
            return;
        }

        let subtotal = 0;
        this.selectedSeats.forEach(s => subtotal += s.calculatedPrice);
        const taxes = Math.round(subtotal * 0.18 * 100) / 100;
        const total = Math.round((subtotal + taxes) * 100) / 100;

        // Payment Request Body
        const payReq = {
            paymentMethod: this.activePaymentMethod,
            amount: total,
            cardNumber: document.getElementById('cardNumInput').value.trim(),
            cardHolder: document.getElementById('cardHolderInput').value.trim() || name,
            expiryDate: document.getElementById('cardExpiryInput').value.trim(),
            cvv: document.getElementById('cardCvvInput').value.trim(),
            upiId: document.getElementById('upiIdInput').value.trim(),
            bankName: document.getElementById('bankSelect').value
        };

        const submitBtn = document.getElementById('paySubmitBtn');
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner" style="display:inline-block;width:14px;height:14px;border:2px solid #fff;border-top-color:transparent;border-radius:50%;animation:spin 0.6s linear infinite;margin-right:8px;"></span> Processing Payment...';

        try {
            // 1. Process simulated payment
            const payRes = await fetch('/api/payments/process', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payReq)
            });
            const payData = await payRes.json();

            if (!payData.success) {
                showToast(payData.message || 'Payment rejected', 'error');
                submitBtn.disabled = false;
                submitBtn.innerHTML = `Pay ₹${total}`;
                return;
            }

            // 2. Book tickets
            const bookingReq = {
                showtimeId: this.currentShowtime.id,
                seatIds: Array.from(this.selectedSeats.keys()),
                customerName: name,
                customerEmail: email,
                customerPhone: phone,
                paymentMethod: this.activePaymentMethod,
                paymentId: payData.data.transactionId,
                userId: Auth.getUser() ? Auth.getUser().id : null
            };

            const bookRes = await fetch('/api/bookings', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(bookingReq)
            });
            const bookData = await bookRes.json();

            if (!bookData.success) {
                showToast(bookData.message || 'Failed to complete booking', 'error');
                submitBtn.disabled = false;
                submitBtn.innerHTML = `Pay ₹${total}`;
                return;
            }

            // Success!
            showToast('Booking Confirmed! Enjoy your movie! 🍿', 'success');
            closeModal('paymentModal');
            this.showTicketConfirmation(bookData.data);
            this.selectedSeats.clear();

        } catch (err) {
            console.error(err);
            showToast('Transaction error. Please try again.', 'error');
        } finally {
            submitBtn.disabled = false;
            submitBtn.innerHTML = `Pay ₹${total}`;
        }
    },

    showTicketConfirmation(booking) {
        const movie = booking.showtime.movie;
        const screen = booking.showtime.screen;
        const cinema = screen.cinema;

        document.getElementById('confirmRefBadge').textContent = `#${booking.bookingReference}`;
        document.getElementById('confirmMoviePoster').src = movie.posterUrl || 'https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800';
        document.getElementById('confirmMovieTitle').textContent = movie.title;
        document.getElementById('confirmCinemaName').textContent = `${cinema ? cinema.name : 'Cinema'} (${screen.screenType})`;
        document.getElementById('confirmDateTime').textContent = `${booking.showtime.showDate} • ${this.formatTime(booking.showtime.startTime)}`;
        
        // Seats
        const seatCodes = booking.bookingSeats.map(bs => bs.seat.seatCode).join(', ');
        document.getElementById('confirmSeats').textContent = `${seatCodes} (${booking.totalSeats} seats)`;
        document.getElementById('confirmTotalPaid').textContent = `₹${booking.totalAmount}`;
        document.getElementById('confirmCustomer').textContent = `${booking.customerName} (${booking.customerEmail})`;
        document.getElementById('confirmTxnId').textContent = booking.paymentId || 'TXN-SUCCESS';

        // QR Code
        const qrData = encodeURIComponent(`CINEPASS|${booking.bookingReference}|${movie.title}|SEATS:${seatCodes}`);
        document.getElementById('confirmQrCode').src = `https://api.qrserver.com/v1/create-qr-code/?size=140x140&data=${qrData}`;

        openModal('confirmationModal');
    },

    async loadUserBookings() {
        const user = Auth.getUser();
        const container = document.getElementById('userBookingsList');
        if (!container) return;

        container.innerHTML = '<div style="text-align:center;padding:40px;color:var(--text-muted);">Loading bookings...</div>';

        try {
            const url = user ? `/api/bookings/user/${user.id}` : `/api/bookings/email?email=user@movieticket.com`;
            const res = await fetch(url);
            const data = await res.json();

            if (!data.success || !data.data || data.data.length === 0) {
                container.innerHTML = `
                    <div class="empty-state">
                        <div class="empty-state-icon">🎟️</div>
                        <h3>No Bookings Found</h3>
                        <p>You haven't booked any movie tickets yet.</p>
                        <button class="btn btn-primary" onclick="App.switchView('home')" style="margin-top:16px;">Browse Movies</button>
                    </div>`;
                return;
            }

            container.innerHTML = '';
            data.data.forEach(booking => {
                const card = document.createElement('div');
                card.className = 'booking-item-card';

                const movie = booking.showtime.movie;
                const screen = booking.showtime.screen;
                const isConfirmed = booking.status === 'CONFIRMED';
                const seatList = booking.bookingSeats ? booking.bookingSeats.map(bs => bs.seat.seatCode).join(', ') : 'Seats';

                card.innerHTML = `
                    <div class="booking-main-info">
                        <img class="booking-poster-mini" src="${movie.posterUrl}" alt="${movie.title}">
                        <div>
                            <div style="display:flex;align-items:center;gap:10px;margin-bottom:4px;">
                                <h3 style="font-size:17px;font-weight:700;">${movie.title}</h3>
                                <span class="status-badge ${isConfirmed ? 'status-confirmed' : 'status-cancelled'}">${booking.status}</span>
                            </div>
                            <div style="font-size:12px;color:var(--text-dim);">
                                Ref: <strong>#${booking.bookingReference}</strong> • ${booking.showtime.showDate} at ${BookingModule.formatTime(booking.showtime.startTime)}
                            </div>
                            <div style="font-size:13px;color:var(--text-muted);margin-top:4px;">
                                ${screen.cinema ? screen.cinema.name : 'Cinema'} • Seats: <strong>${seatList}</strong>
                            </div>
                        </div>
                    </div>
                    <div style="display:flex;align-items:center;gap:14px;flex-wrap:wrap;">
                        <div style="text-align:right;">
                            <div style="font-size:11px;color:var(--text-dim);">Total Paid</div>
                            <div style="font-size:18px;font-weight:800;color:var(--accent-gold);">₹${booking.totalAmount}</div>
                        </div>
                        <button class="btn btn-secondary btn-sm" onclick="BookingModule.viewTicketModal('${booking.bookingReference}')">View Ticket</button>
                        ${isConfirmed ? `<button class="btn btn-danger btn-sm" onclick="BookingModule.cancelBooking('${booking.bookingReference}')">Cancel</button>` : ''}
                    </div>
                `;
                container.appendChild(card);
            });
        } catch (err) {
            console.error(err);
            container.innerHTML = '<div style="text-align:center;padding:30px;color:#ef4444;">Failed to load bookings</div>';
        }
    },

    async viewTicketModal(reference) {
        try {
            const res = await fetch(`/api/bookings/${reference}`);
            const data = await res.json();
            if (data.success) {
                this.showTicketConfirmation(data.data);
            } else {
                showToast('Unable to view ticket', 'error');
            }
        } catch (err) {
            showToast('Error loading ticket', 'error');
        }
    },

    async cancelBooking(reference) {
        if (!confirm(`Are you sure you want to cancel booking #${reference}? Seats will be released and a full refund will be processed.`)) {
            return;
        }

        try {
            const res = await fetch(`/api/bookings/${reference}/cancel`, { method: 'POST' });
            const data = await res.json();
            if (data.success) {
                showToast(data.message, 'success');
                this.loadUserBookings();
            } else {
                showToast(data.message || 'Failed to cancel', 'error');
            }
        } catch (err) {
            showToast('Unable to cancel booking', 'error');
        }
    },

    printTicket() {
        window.print();
    },

    formatTime(timeStr) {
        if (!timeStr) return '';
        const parts = timeStr.split(':');
        let hours = parseInt(parts[0], 10);
        const minutes = parts[1];
        const ampm = hours >= 12 ? 'PM' : 'AM';
        hours = hours % 12;
        hours = hours ? hours : 12;
        return `${hours}:${minutes} ${ampm}`;
    }
};

window.BookingModule = BookingModule;

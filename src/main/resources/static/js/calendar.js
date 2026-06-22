document.addEventListener('DOMContentLoaded', function () {

    // ===== Calendrier gîte — sélection de dates =====
    const calDays = document.querySelectorAll('.cal-day.avail');
    const checkInInput = document.querySelector('input[name="checkIn"]');
    const checkOutInput = document.querySelector('input[name="checkOut"]');

    if (calDays.length === 0 || !checkInInput) return;

    calDays.forEach(function (btn) {
        btn.addEventListener('click', function () {

            const params = new URLSearchParams(window.location.search);
            const year = params.get('year') || new Date().getFullYear();
            const month = params.get('month') || (new Date().getMonth() + 1);
            const day = this.textContent.trim();
            const date = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;

            if (!checkInInput.value) {
                checkInInput.value = date;
                checkOutInput.value = '';
            } else if (!checkOutInput.value && date > checkInInput.value) {
                checkOutInput.value = date;
            } else {
                checkInInput.value = date;
                checkOutInput.value = '';
            }

            highlightRange();
        });
    });

    checkInInput.addEventListener('change', highlightRange);
    checkOutInput.addEventListener('change', highlightRange);

    /**
     * Met en évidence la période sélectionnée sur le calendrier.
     */
    function highlightRange() {
        const params = new URLSearchParams(window.location.search);
        const year = parseInt(params.get('year') || new Date().getFullYear());
        const month = parseInt(params.get('month') || (new Date().getMonth() + 1));

        const checkIn = checkInInput.value;
        const checkOut = checkOutInput.value;

        calDays.forEach(function (btn) {
            const day = parseInt(btn.textContent.trim());
            const date = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;

            btn.classList.remove('cal-day-start', 'cal-day-end', 'cal-day-range');

            if (checkIn && date === checkIn) {
                btn.classList.add('cal-day-start');
            } else if (checkOut && date === checkOut) {
                btn.classList.add('cal-day-end');
            } else if (checkIn && checkOut && date > checkIn && date < checkOut) {
                btn.classList.add('cal-day-range');
            }
        });
    }
});
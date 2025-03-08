document.getElementById('reservation-form').addEventListener('submit', function(event) {
    event.preventDefault();
    const name = document.getElementById('name').value;
    const people = document.getElementById('people').value;
    const date = document.getElementById('date').value;

    fetch('/api/reservations', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ name, numberOfPeople: people, reservationTime: date }),
    })
    .then(response => response.json())
    .then(data => {
        alert('Столик успешно забронирован!');
    })
    .catch((error) => {
        console.error('Ошибка:', error);
    });
});

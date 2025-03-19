const API_URL = "http://localhost:8080";

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('reservation-form').addEventListener('submit', function(event) {
        event.preventDefault();
        
        // Получаем значения из формы
        const name = document.getElementById('name').value; // Имя пользователя
        const people = document.getElementById('people').value; // Количество человек
        const reservationTime = document.getElementById('date').value; // Время бронирования
        const tableId = document.getElementById('tableId').value; // ID столика

        // Делаем запрос на сервер
        fetch(`${API_URL}/api/reservations`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ 
                user_id: 1, // Укажите корректный user_id
                table_id: tableId, 
                reservation_time: reservationTime, 
                number_of_people: people 
            }), // Передаем данные с соответствующими именами полей
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Сетевая ошибка: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            alert('Столик успешно забронирован!');
        })
        .catch(error => {
            console.error('Ошибка:', error);
            alert('Ошибка при бронировании столика.');
        });
    });
});

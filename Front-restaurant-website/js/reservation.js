const API_URL = "http://localhost:8080";

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('reservation-form').addEventListener('submit', async function(event) {
        event.preventDefault();

        const token = localStorage.getItem('token');
        if (!token) {
            alert('Ошибка: Вы не авторизованы. Пожалуйста, войдите в систему.');
            return;
        }

        const people = parseInt(document.getElementById('people').value, 10);
        const reservationTime = document.getElementById('date').value;
        const tableId = document.getElementById('tableId').value;

        const requestData = { 
            table: { id: tableId }, 
            reservationTime: reservationTime, 
            numberOfPeople: people 
        };

        try {
            const response = await fetch(`${API_URL}/api/reserve`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(requestData),
            });

            const responseData = await response.json();

            if (!response.ok) {
                // Отображаем ошибку с более подробной информацией
                alert(responseData.error || 'Ошибка при бронировании столика. Пожалуйста, попробуйте снова.');
                return;
            }

            alert(responseData.message || 'Столик успешно забронирован!');
        } catch (error) {
            // Обрабатываем сетевые или неожиданные ошибки
            console.error('Ошибка при бронировании:', error);
            if (error.name === 'TypeError' && error.message === 'Failed to fetch') {
                alert('Не удалось выполнить запрос. Пожалуйста, проверьте ваше соединение с сервером.');
            } else {
                alert('Ошибка при бронировании столика: ' + error.message);
            }
        }
    });
});

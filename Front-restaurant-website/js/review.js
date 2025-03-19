const API_URL = "http://localhost:8080";

document.getElementById('review-form').addEventListener('submit', function(event) {
    event.preventDefault();
    const review = document.getElementById('review').value;

    fetch(`${API_URL}/api/reviews`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ review }),
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Сетевая ошибка: ' + response.status);
        }
        return response.json();
    })
    .then(data => {
        alert('Отзыв успешно отправлен!');
    })
    .catch(error => {
        console.error('Ошибка:', error);
    });
});

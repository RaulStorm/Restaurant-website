document.getElementById('review-form').addEventListener('submit', function(event) {
    event.preventDefault();
    const review = document.getElementById('review').value;

    fetch('/api/reviews', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ review }),
    })
    .then(response => response.json())
    .then(data => {
        alert('Отзыв успешно отправлен!');
    })
    .catch((error) => {
        console.error('Ошибка:', error);
    });
});

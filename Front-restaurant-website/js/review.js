document.getElementById('review-form').addEventListener('submit', function(event) {
    event.preventDefault();

    const review = document.getElementById('review').value;
    const rating = document.querySelector('input[name="rating"]:checked')?.value;

    if (!rating) {
        alert('Пожалуйста, выберите рейтинг.');
        return;
    }

    const token = localStorage.getItem("token");

    fetch("http://192.168.1.70:8080/api/reviews", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ reviewText: review, rating: parseInt(rating) })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Ошибка: ' + response.status);
        }
        return response.json();
    })
    .then(data => {
        alert("Отзыв успешно отправлен!");
        document.getElementById('review-form').reset();
    })
    .catch(error => {
        console.error("Ошибка отправки:", error);
        alert("Не удалось отправить отзыв.");
    });
});

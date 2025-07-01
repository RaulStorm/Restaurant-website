let currentImageIndex = 0; // Инициализация текущего индекса

function changeImage(direction) {
    const images = document.querySelectorAll('.carousel-images img');

    // Убираем класс активности с текущего изображения
    images[currentImageIndex].classList.remove('active');

    // Изменяем индекс текущего изображения
    currentImageIndex = (currentImageIndex + direction + images.length) % images.length;

    // Добавляем класс активности к новому изображению
    images[currentImageIndex].classList.add('active');
}

// Инициализация первого изображения как активного
document.addEventListener("DOMContentLoaded", () => {
    const images = document.querySelectorAll('.carousel-images img');
    images[currentImageIndex].classList.add('active'); // Установить первое изображение как активное
});


async function loadReviews() {
    try {
        const response = await fetch('http://192.168.1.70:8080/api/reviews/latest');
        if (!response.ok) throw new Error("Ошибка загрузки отзывов");

        const reviews = await response.json();
        const container = document.getElementById('review-list');

        if (reviews.length === 0) {
            container.innerHTML = "<p>Отзывов пока нет.</p>";
            return;
        }

        reviews.forEach(review => {
            const card = document.createElement('div');
            card.className = 'review-card';

            const user = review.user?.name || "Аноним";
            const stars = "★".repeat(review.rating);
            const date = review.formattedDate || new Date(review.createdAt).toLocaleDateString('ru-RU');

            card.innerHTML = `
                <div class="review-user">${user}</div>
                <div class="review-date">${date}</div>
                <div class="review-rating">${stars}</div>
                <div class="review-text">${review.reviewText}</div>
            `;

            container.appendChild(card);
        });

    } catch (err) {
        console.error("Ошибка загрузки отзывов:", err);
    }
}

document.addEventListener('DOMContentLoaded', loadReviews);

let currentIndex = 0;

function changeImage(direction) {
    const images = document.querySelectorAll('.carousel-images img');
    const totalImages = images.length;

    currentIndex = (currentIndex + direction + totalImages) % totalImages; // Цикличность индекса

    const newOffset = currentIndex * images[0].clientWidth; // Позиция сдвига
    const carouselImages = document.querySelector('.carousel-images');
    
    // Плавно перемещаем карусель
    carouselImages.style.transform = `translateX(-${newOffset}px)`;
}

// Автоматическая прокрутка с плавным эффектом
setInterval(() => {
    changeImage(1); // 1 означает прокрутку вправо (перелистывание)
}, 10000); // Интервал 10000 миллисекунд (10 секунды)

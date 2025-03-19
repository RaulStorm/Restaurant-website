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

    // Обработчики событий для кнопок
    document.querySelector('button.prev').addEventListener('click', () => changeImage(-1));
    document.querySelector('button.next').addEventListener('click', () => changeImage(1));
});

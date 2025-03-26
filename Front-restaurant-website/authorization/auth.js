const API_URL = "http://localhost:8080"; // Убедитесь, что этот URL правильный и доступен

document.addEventListener('DOMContentLoaded', function() {
    const sign_in_btn = document.querySelector("#sign-in-btn");
    const sign_up_btn = document.querySelector("#sign-up-btn");
    const container = document.querySelector(".container");

    sign_up_btn.addEventListener('click', () => {
        container.classList.add("sign-up-mode");
    });

    sign_in_btn.addEventListener('click', () => {
        container.classList.remove("sign-up-mode");
    });

    const signInForm = document.querySelector('.sign-in-form');
    signInForm.addEventListener('submit', function(event) {
        event.preventDefault();
        const emailInput = signInForm.querySelector('input[name="email"]');
        const passwordInput = signInForm.querySelector('input[name="password"]');

        if (!emailInput || !passwordInput) {
            console.error('Не удалось получить значения формы');
            return;
        }

        const email = emailInput.value.trim();
        const password = passwordInput.value.trim();

        fetch(`${API_URL}/api/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        })
        .then(response => response.ok ? response.json() : Promise.reject(response.json()))
        .then(data => {
            if (data.success && data.token) {
                localStorage.setItem('token', data.token); // Сохраняем токен
                localStorage.setItem('user', JSON.stringify(data.user)); // Сохраняем данные пользователя
                window.location.href = '/index.html'; // Переход на главную страницу
            } else {
                console.error('Ошибка входа:', data.message || 'Неизвестная ошибка');
            }
        })
        .catch(error => console.error('Ошибка:', error));
    });

    const signUpForm = document.querySelector('.sign-up-form');
    signUpForm.addEventListener('submit', function(event) {
        event.preventDefault();
        const nameInput = signUpForm.querySelector('input[name="name"]');
        const emailInput = signUpForm.querySelector('input[name="email"]');
        const passwordInput = signUpForm.querySelector('input[name="password"]');

        if (!nameInput || !emailInput || !passwordInput) {
            console.error('Не удалось получить значения формы');
            return;
        }

        const name = nameInput.value.trim();
        const email = emailInput.value.trim();
        const password = passwordInput.value.trim();

        fetch(`${API_URL}/api/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name, email, password })
        })
        .then(response => response.ok ? response.json() : Promise.reject(response.json()))
        .then(data => {
            if (data.success) {
                console.log('Регистрация выполнена');
            } else {
                console.error('Ошибка регистрации:', data.message || 'Неизвестная ошибка');
            }
        })
        .catch(error => console.error('Ошибка:', error));
    });
});

// Функция для обновления UI после загрузки страницы
document.addEventListener('DOMContentLoaded', updateUserUI); 

function updateUserUI() {
    const userIcon = document.querySelector('#user-icon'); // Найди элемент с id="user-icon"
    const user = JSON.parse(localStorage.getItem('user'));

    if (user) {
        userIcon.src = '/authorization/img/avatar-icon.png'; // Меняем на иконку личного кабинета
        userIcon.alt = 'Личный кабинет';
        userIcon.onclick = () => window.location.href = '/profile.html'; // Переход в личный кабинет
    } else {
        userIcon.src = '/authorization/img/auth.png'; // Возвращаем иконку входа
        userIcon.alt = 'Вход';
        userIcon.onclick = () => window.location.href = '/authorization/auth.html'; // Переход на страницу входа
    }
}

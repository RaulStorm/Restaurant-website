const API_URL = "http://localhost:8080"; // Убедитесь, что этот URL правильный и доступен

document.addEventListener('DOMContentLoaded', function () {
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
    signInForm.addEventListener('submit', function (event) {
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
    signUpForm.addEventListener('submit', function (event) {
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

    // Функция для обновления UI после загрузки страницы
    updateUserUI();
});


document.addEventListener('DOMContentLoaded', function() {
    const sign_in_btn = document.querySelector("#sign-in-btn");
    const sign_up_btn = document.querySelector("#sign-up-btn");
    const container = document.querySelector(".container");

    // Проверяем, что элементы существуют перед добавлением обработчиков событий
    if (sign_up_btn && sign_in_btn && container) {
        sign_up_btn.addEventListener('click', () => {
            container.classList.add("sign-up-mode");
        });

        sign_in_btn.addEventListener('click', () => {
            container.classList.remove("sign-up-mode");
        });
    } else {
        console.error('Элементы не найдены!');
    }

    // Добавление обработчика для формы авторизации
    const form = document.querySelector('#auth-form');
    if (form) {
        form.addEventListener('submit', handleAuthSubmit);
    } else {
        console.error('Форма авторизации не найдена!');
    }
});

// Обработка события отправки формы (вход или регистрация)
function handleAuthSubmit(event) {
    event.preventDefault(); // Отменяем стандартное поведение формы

    const form = event.target;
    const email = form.querySelector('#email').value;
    const password = form.querySelector('#password').value;
    
    // Проверка валидности данных (можно добавить дополнительные проверки)
    if (!email || !password) {
        alert('Пожалуйста, заполните все поля');
        return;
    }

    // Эмулируем процесс авторизации (или регистрации)
    const user = {
        email: email,
        password: password,
        // Добавляем другие данные пользователя, если нужно
    };

    // Пример сохранения пользователя в localStorage
    localStorage.setItem('user', JSON.stringify(user));

    // После авторизации обновляем UI
    updateUserUI();
}

// Функция для обновления UI (иконки пользователя)
function updateUserUI() {
    const userIcon = document.querySelector('#user-icon');
    const userData = localStorage.getItem('user');
    
    // Если данные о пользователе есть в localStorage, обновляем UI
    const user = userData ? JSON.parse(userData) : null;

    if (user && userIcon) {
        userIcon.src = '/authorization/img/avatar-icon.png';
        userIcon.alt = 'Личный кабинет';
        userIcon.onclick = () => window.location.href = '/profile.html';
    } else if (userIcon) {
        userIcon.src = '/authorization/img/auth.png';
        userIcon.alt = 'Вход';
        userIcon.onclick = () => window.location.href = '/authorization/auth.html';
    }
}

// Функция для выхода из аккаунта (деактивация токена)
function logout() {
    localStorage.removeItem('user'); // Удаляем информацию о пользователе
    updateUserUI(); // Обновляем UI после выхода
}

// Проверка, есть ли пользователь в localStorage и обновление UI
if (localStorage.getItem('user')) {
    updateUserUI();
}

// Загрузка header и footer через fetch
fetch('/страницы/header.html')
    .then(response => response.text())
    .then(data => {
        document.querySelector('header').innerHTML = data;
        // Обновляем UI после загрузки header
        updateUserUI();
    })
    .catch(err => console.error('Ошибка загрузки header:', err));

fetch('/страницы/footer.html')
    .then(response => response.text())
    .then(data => {
        document.querySelector('footer').innerHTML = data;
    })
    .catch(err => console.error('Ошибка загрузки footer:', err));

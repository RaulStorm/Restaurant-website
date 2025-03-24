const API_URL = "http://localhost:8080"; // Убедитесь, что этот URL правильный и доступен из вашего браузера

const sign_in_btn = document.querySelector("#sign-in-btn");
const sign_up_btn = document.querySelector("#sign-up-btn");
const container = document.querySelector(".container");

sign_up_btn.addEventListener('click', () =>{
    container.classList.add("sign-up-mode");
});

sign_in_btn.addEventListener('click', () =>{
    container.classList.remove("sign-up-mode");
});

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

    // Обработчик отправки формы входа
    const signInForm = document.querySelector('.sign-in-form');
    signInForm.addEventListener('submit', function(event) {
        event.preventDefault();
        const username = signInForm.querySelector('input[type="text"]').value;
        const password = signInForm.querySelector('input[type="password"]').value;
        
        fetch(`${API_URL}/api/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                console.log('Вход выполнен');
                // Добавьте вашу логику при успешном входе
            } else {
                console.error('Ошибка входа', data.message);
            }
        })
        .catch(error => console.error('Ошибка:', error));
    });

    // Обработчик отправки формы регистрации
    const signUpForm = document.querySelector('.sign-up-form');
    signUpForm.addEventListener('submit', function(event) {
        event.preventDefault();
        const username = signUpForm.querySelector('input[type="text"]').value;
        const email = signUpForm.querySelector('input[type="email"]').value;
        const password = signUpForm.querySelector('input[type="password"]').value;

        fetch(`${API_URL}/api/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, email, password })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                console.log('Регистрация выполнена');
                // Добавьте вашу логику при успешной регистрации
            } else {
                console.error('Ошибка регистрации', data.message);
            }
        })
        .catch(error => console.error('Ошибка:', error));
    });
});

const API_URL = "http://localhost:8080"; // Убедитесь, что этот URL правильный и доступен из вашего браузера
let cart = []; // Массив для хранения позиций в корзине

document.addEventListener('DOMContentLoaded', async () => {
    try {
        const response = await fetch(`${API_URL}/api/menu`);
        if (!response.ok) {
            throw new Error('Сетевая ошибка: ' + response.status);
        }
        const data = await response.json();
        console.log('Полученные данные:', data); // Лог для проверки данных

        const menuItemsContainer = document.getElementById('menu-items');
        const categories = {};

        // Группируем блюда по категориям
        data.forEach(item => {
            const categoryName = item.categoryName || 'Другие'; // Предполагается наличие поля categoryName
            if (!categories[categoryName]) {
                categories[categoryName] = [];
            }
            categories[categoryName].push(item);
        });

        // Создаем вкладки для категорий и секции для меню
        createTabButtons(Object.keys(categories));
        createCategorySections(categories, menuItemsContainer);
        attachCartListeners(); // Подключаем слушатели после генерации элементов меню
    } catch (error) {
        console.error('Ошибка:', error);
    }
});

// Создание кнопок вкладок категорий
function createTabButtons(categoryNames) {
    const tabsContainer = document.querySelector('.tabs');

    categoryNames.forEach(name => {
        const tabButton = document.createElement('button');
        tabButton.className = 'tab-button';
        tabButton.textContent = name;
        tabButton.dataset.category = name;

        tabButton.addEventListener('click', () => {
            showItems(name);
            setActiveTab(tabButton);
        });

        tabsContainer.appendChild(tabButton);
    });

    const allButton = document.createElement('button');
    allButton.className = 'tab-button active';
    allButton.textContent = 'Все';
    allButton.dataset.category = 'all';
    allButton.addEventListener('click', () => {
        showItems('all');
        setActiveTab(allButton);
    });
    tabsContainer.prepend(allButton);
}

// Создание секций для каждой категории и их элементов
function createCategorySections(categories, menuItemsContainer) {
    for (const category in categories) {
        const categoryDiv = document.createElement('div');
        categoryDiv.className = 'category';
        categoryDiv.dataset.category = category;

        categories[category].forEach(item => {
            const menuItemDiv = document.createElement('div');
            menuItemDiv.className = 'menu-item';

            const imageUrl = item.images && item.images.length > 0 ? item.images[0] : '/default-image.jpg'; // Поддержка отсутствия изображения

            menuItemDiv.innerHTML = `
                <div class="menu-card">
                    <img src="${imageUrl}" alt="${item.name}" class="menu-image" loading="lazy"/>
                    <h4>${item.name}</h4>
                    <p>${item.description}</p>
                    <div class="price"><span>${item.price.toFixed(2)}</span> ₽</div>
                    <input type="number" class="item-quantity" min="1" value="1">
                    <button class="add-to-cart" data-id="${item.id}">Добавить в корзину</button>
                </div>
            `;
            categoryDiv.appendChild(menuItemDiv);
        });

        menuItemsContainer.appendChild(categoryDiv);
    }

    showItems('all'); // Изначально отображаем все элементы
}

// Отображение элементов по категориям
function showItems(category) {
    const categories = document.querySelectorAll('.category');
    categories.forEach(cat => {
        if (category === 'all' || cat.dataset.category === category) {
            cat.classList.add('active');
        } else {
            cat.classList.remove('active');
        }
    });
}

// Установка активной вкладки
function setActiveTab(activeButton) {
    const tabButtons = document.querySelectorAll('.tab-button');
    tabButtons.forEach(button => button.classList.remove('active'));
    activeButton.classList.add('active');
}

// Подключение слушателей для добавления товаров в корзину
function attachCartListeners() {
    const addToCartButtons = document.querySelectorAll('.add-to-cart');
    addToCartButtons.forEach(button => {
        button.addEventListener('click', handleAddToCart);
    });
}

// Обработка добавления товаров в корзину
function handleAddToCart(event) {
    const itemId = event.target.dataset.id;
    const itemContainer = event.target.closest('.menu-item');
    const itemName = itemContainer.querySelector('h4').textContent;
    const itemPrice = parseFloat(itemContainer.querySelector('.price span').textContent);
    const itemQuantity = parseInt(itemContainer.querySelector('.item-quantity').value);

    // Проверка корректности введенного количества
    if (isNaN(itemQuantity) || itemQuantity <= 0) {
        alert("Пожалуйста, введите корректное количество товаров.");
        return;
    }

    const itemInCart = cart.find(i => i.id === itemId);

    if (itemInCart) {
        itemInCart.quantity += itemQuantity;
    } else {
        cart.push({ id: itemId, name: itemName, price: itemPrice, quantity: itemQuantity });
    }

    console.log('Корзина:', cart); // Лог для проверки корзины
    updateCart(); // Обновление отображения корзины
}

// Обновление отображения корзины
function updateCart() {
    const cartItemsContainer = document.querySelector('#cart-items');
    const totalPriceContainer = document.querySelector('#total-price');

    cartItemsContainer.innerHTML = ''; // Очистка текущих значений

    let totalPrice = 0;

    cart.forEach(item => {
        const itemRow = document.createElement('div');
        itemRow.textContent = `${item.name} x ${item.quantity} - ${item.price * item.quantity} ₽`;

        cartItemsContainer.appendChild(itemRow);

        totalPrice += item.price * item.quantity;
    });

    totalPriceContainer.textContent = totalPrice.toFixed(2); // Установка общей суммы
}

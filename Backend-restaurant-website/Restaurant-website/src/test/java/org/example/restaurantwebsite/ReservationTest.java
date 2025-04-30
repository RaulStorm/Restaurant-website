package org.example.restaurantwebsite;

import org.example.restaurantwebsite.controller.ReservationController;
import org.example.restaurantwebsite.model.Reservation;
import org.example.restaurantwebsite.model.RestaurantTable;
import org.example.restaurantwebsite.model.User;
import org.example.restaurantwebsite.repository.RestaurantTableRepository;
import org.example.restaurantwebsite.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ReservationTest {

    @InjectMocks
    private ReservationController reservationController; // Замените на ваш контроллер бронирования

    @Mock
    private RestaurantTableRepository restaurantTableRepository;

    @Mock
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        // Инициализация MockMvc с вашим контроллером
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();
    }

    @Test
    public void testAuthorizeAndBookTables() throws Exception {
        // Шаг 1: Авторизация с логином и паролем
        String login = "r@gmail.com";
        String password = "123";

        // Мокаем пользователя
        User user = mock(User.class);
        when(userRepository.findByEmail(login)).thenReturn(Optional.of(user));
        when(user.getPassword()).thenReturn(password);

        // Выполняем авторизацию через POST запрос
        mockMvc.perform(post("/login")
                        .param("email", login)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(content().string("Авторизация прошла успешно"));

        // Шаг 2: Бронирование столиков для всех доступных столиков на 15.05.2025
        String reservationDate = "15.05.2025";
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date reservationDateParsed = sdf.parse(reservationDate);

        // Мокаем столики
        when(restaurantTableRepository.findAll()).thenReturn(getAllTables());

        // Мокаем, что пользователю разрешено бронирование
        when(userRepository.findByEmail(login)).thenReturn(Optional.of(user));

        // Создаём бронирования для всех столиков
        for (int i = 0; i < 3; i++) {  // Предполагаем, что у нас есть 3 стола
            mockMvc.perform(post("/reserve")
                            .param("date", reservationDate)
                            .param("people", "4")
                            .param("tableId", String.valueOf(i + 1)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Бронирование успешно"));
        }
    }

    // Метод для получения списка всех столиков
    private List<RestaurantTable> getAllTables() {
        return Arrays.asList(
                new RestaurantTable(1L, "Table 1", 4),
                new RestaurantTable(2L, "Table 2", 4),
                new RestaurantTable(3L, "Table 3", 4)
        );
    }
}

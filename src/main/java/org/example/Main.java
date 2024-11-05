import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

class Main {

    public static void main(String[] args) {
        List<User> users = loadUsersFromJson("books.json");

        if (users != null) {
            displayUsersAndFavoriteBooks(users);
            displayTotalVisitors(users);
            displayUniqueFavoriteBooks(users);
            checkAuthorInFavorites(users, "Jane Austen");
            displayMaxFavoriteBooks(users);
            displayBooksSortedByYear(users);
            sendSMS(users);
        }
    }

    private static List<User> loadUsersFromJson(String filePath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            Type userListType = new TypeToken<List<User>>() {}.getType();
            return gson.fromJson(reader, userListType);
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке данных: " + e.getMessage());
            return null;
        }
    }

    private static void displayUsersAndFavoriteBooks(List<User> users) {
        System.out.println("Список посетителей:");
        for (User user : users) {
            System.out.println(user.getName() + " " + user.getSurname());
            System.out.println("Любимые книги:");
            for (Book book : user.getFavoriteBooks()) {
                System.out.println(" - " + book.getName() + " by " + book.getAuthor());
            }
            System.out.println();
        }
    }

    private static void displayTotalVisitors(List<User> users) {
        System.out.println("Общее количество посетителей: " + users.size());
    }

    private static void displayUniqueFavoriteBooks(List<User> users) {
        Set<String> uniqueBooks = new HashSet<>();
        for (User user : users) {
            for (Book book : user.getFavoriteBooks()) {
                uniqueBooks.add(book.getName() + " by " + book.getAuthor());
            }
        }
        System.out.println("Количество уникальных книг в избранном: " + uniqueBooks.size());
    }

    private static void checkAuthorInFavorites(List<User> users, String author) {
        boolean hasAuthorBook = users.stream()
                .anyMatch(user -> user.getFavoriteBooks().stream()
                        .anyMatch(book -> book.getAuthor().equals(author)));
        System.out.println("Есть ли у кого-то в избранном книга автора '" + author + "': " + hasAuthorBook);
    }

    private static void displayMaxFavoriteBooks(List<User> users) {
        int maxFavorites = users.stream()
                .mapToInt(user -> user.getFavoriteBooks().size())
                .max()
                .orElse(0);
        System.out.println("Максимальное количество книг в избранном: " + maxFavorites);
    }

    private static void displayBooksSortedByYear(List<User> users) {
        List<Book> allBooks = users.stream()
                .flatMap(user -> user.getFavoriteBooks().stream())
                .distinct()
                .sorted(Comparator.comparingInt(Book::getPublishingYear))
                .collect(Collectors.toList());

        System.out.println("Книги, отсортированные по году издания:");
        for (Book book : allBooks) {
            System.out.println(book.getPublishingYear() + ": " + book.getName() + " by " + book.getAuthor());
        }
    }

    private static void sendSMS(List<User> users) {
        for (User user : users) {
            String message = user.getFavoriteBooks().size() >= 5 ? "you are a bookworm" : "read more";
            System.out.println("To: " + user.getPhone() + ", Message: " + message);
        }
    }
}

// Класс для представления пользователя
class User {
    @Getter
    private String name;
    @Getter
    private String surname;
    @Getter
    private String phone;
    private boolean subscribed;
    @Getter
    private List<Book> favoriteBooks;

}

// Класс для представления книги
class Book {
    @Getter
    private String name;
    @Getter
    private String author;
    @Getter
    private int publishingYear;
    private String isbn;
    private String publisher;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(name, book.name) && Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, author);
    }
}
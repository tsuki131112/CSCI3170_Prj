package dataObject;

import java.util.List;

public record Book(String ISBN, String title, int quantity, int price) {
    public static void outputList(List<Book> books) {
        System.out.println("List of books:");
        for (Book book : books) {
            System.out.print("\t");
            System.out.println(book);
        }
    }
}

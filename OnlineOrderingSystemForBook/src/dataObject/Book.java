package dataObject;

import java.util.List;

public record Book(String ISBN, String title, String author, int quantity, int price) {
    public static void outputList(List<Book> books) {
        System.out.println("List of books (" + books.size() + " books):");
        for (Book book : books) {
            System.out.print("\t");
            System.out.println(book);
        }
    }
}

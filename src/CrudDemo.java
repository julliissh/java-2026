import entity.BaseEntity;
import exception.EntityNotFoundException;
import product.Clothing;
import product.Electronics;
import product.Product;
import service.EntityUtils;
import service.InMemoryCrudService;
import service.InMemoryProductService;

import java.util.Comparator;

public class CrudDemo {

    void main() {
        System.out.println("=== 1. Тестування ProductService (Рівні 1-3) ===");
        InMemoryProductService productService = new InMemoryProductService();

        Product p1 = new Clothing(1, "Футболка", 600.0, 10, "M", "Бавовна");
        Product p2 = new Electronics(2, "Ноутбук", 35000.0, 3, 24);
        Product p3 = new Clothing(3, "Футболка", 450.0, 15, "L", "Поліестер");

        productService.save(p1);
        productService.save(p2);
        productService.save(p3);
        System.out.println("Кількість після додавання: " + productService.count());
        EntityUtils.printAll(productService.findAll());

        System.out.println("\n--- Оновлення товару (id=1) ---");
        Product p1Updated = new Clothing(1, "Футболка", 550.0, 12, "M", "Бавовна");
        System.out.println("Кількість до оновлення: " + productService.count());
        productService.save(p1Updated);
        System.out.println("Кількість після оновлення: " + productService.count());
        System.out.println("Оновлений товар: " + productService.findById(1));

        System.out.println("\n--- Пошук неіснуючого id=999 ---");
        try {
            productService.findById(999);
        } catch (EntityNotFoundException e) {
            System.out.println("Спіймано виняток: " + e.getMessage() + " (id=" + e.getId() + ")");
        }

        System.out.println("\n--- Стан сховища ДО сортування ---");
        EntityUtils.printAll(productService.findAll());

        System.out.println("\n--- Сортування за назвою: findAllSortedByName() [default-метод] ---");
        EntityUtils.printAll(productService.findAllSortedByName());

        System.out.println("\n--- Вторинне сортування: findAllSortedByNameAndByPrice() ---");
        EntityUtils.printAll(productService.findAllSortedByNameAndByPrice());

        System.out.println("\n--- Стан сховища ПІСЛЯ сортувань (порядок не змінився) ---");
        EntityUtils.printAll(productService.findAll());

        System.out.println("\n--- Узагальнене сортування findAllSorted з Comparator<BaseEntity> (? super T) ---");
        Comparator<BaseEntity> idDescComparator = Comparator.comparingLong(BaseEntity::getId).reversed();
        EntityUtils.printAll(productService.findAllSorted(idDescComparator));

        System.out.println("\n--- Видалення товару ---");
        System.out.println("Видалення id=2 (успішне): " + productService.delete(2));
        System.out.println("Видалення id=2 (повторне): " + productService.delete(2));
        System.out.println("Видалення id=999 (неіснуюче): " + productService.delete(999));
        System.out.println("Кількість після видалення: " + productService.count());

        System.out.println("\n=== 2. Тестування сервісу клієнтів (Customer) ===");
        InMemoryCrudService<Customer> customerService = new InMemoryCrudService<>();

        Customer c1 = new Customer(1, "Олена Коваль", "0991234567");
        Customer c2 = new Customer(2, "Богдан Шевченко", "0671234567");
        Customer c3 = new Customer(3, "Андрій Мельник", "0507654321");

        customerService.save(c1);
        customerService.save(c2);
        customerService.save(c3);
        System.out.println("Кількість клієнтів: " + customerService.count());
        EntityUtils.printAll(customerService.findAll());

        Customer c1Updated = new Customer(1, "Олена Грищенко", "0991234567");
        System.out.println("Кількість до оновлення: " + customerService.count());
        customerService.save(c1Updated);
        System.out.println("Кількість після оновлення: " + customerService.count());
        System.out.println("Оновлений клієнт id=1: " + customerService.findById(1));

        System.out.println("\n--- Клієнти, відсортовані за іменем через default-метод ---");
        EntityUtils.printAll(customerService.findAllSortedByName());
    }
}

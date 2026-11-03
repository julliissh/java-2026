# Лабораторна робота №7-9: Реалізація типізованого CRUD-сервісу

## Предметна область та опис сутностей

У системі визначено спільний базовий клас `BaseEntity` та дві сутності предметної області інтернет-магазину:

1. **`Product` (Товар)** — абстрактний клас товару:
   - Успадковує `id` та `name` від `BaseEntity`.
   - Зберігає ціну (`price > 0`) та залишок на складі (`quantity > 0`).
   - Має запечатану (sealed) ієрархію спеціалізованих категорій:
     - **`Clothing` (Одяг)** — додає розмір (`size`) та матеріал тканини (`material`).
     - **`Electronics` (Електроніка)** — додає гарантійний термін (`warrantyMonths`).
     - **`Smartphone` (Смартфон)** — нащадок `Electronics`, додає обсяг вбудованої пам'яті (`storageGb`) та ємність акумулятора (`batteryCapacity`).
2. **`Customer` (Покупець)** — сутність клієнта магазину:
   - Успадковує `id` та `name` від `BaseEntity`.
   - Зберігає номер телефону (`private final String phone`) із суворою перевіркою регулярним виразом під формат українських мобільних номерів.

---

## Рівень 1. Базовий (обов'язковий мінімум)

1. **`BaseEntity`** (`entity/BaseEntity.java`):
   - `private final long id` — незмінний ідентифікатор після створення.
   - `private final String name` — назва або ім'я (перевірка `name == null || name.trim().isEmpty()`).
   - Конструктор та гетери `getId()`, `getName()`.
   - Методи `equals(Object obj)` та `hashCode()` перевизначені за `id` з перевіркою `getClass()`, щоб уникнути помилкової рівності між різними типами сутностей (наприклад, `Product` і `Customer`) з однаковим `id`.
   - Сутності `Product` та `Customer` успадковують `BaseEntity`.

2. **`EntityNotFoundException`** (`exception/EntityNotFoundException.java`):
   - Містить поле `id` та інформативне повідомлення.
   - **Обґрунтування unchecked:** `EntityNotFoundException extends ShopException extends RuntimeException`. Unchecked, бо `findById` викликається в багатьох місцях, і checked-виняток змусив би кожен виклик обгортати в `try-catch` або прокидати `throws`. Checked-винятки також незручні в лямбдах. Мінус: компілятор не змушує обробити виняток, тому його потрібно перехоплювати вручну там, де це необхідно, як у `CrudDemo`.

3. **`CRUDService<T extends BaseEntity>`** (`service/CRUDService.java`):
   - Інтерфейс із методами: `findById`, `save`, `delete`, `findAllSorted`, `findAllSortedByName`.
   - **Навіщо потрібне обмеження `T extends BaseEntity`:** Без цього обмеження тип `T` стирався б компілятором до `Object`. Всередині сервісу у методі `save(T entity)` було б неможливо викликати метод `entity.getId()`, щоб використати його як ключ у мапі (`storage.put(entity.getId(), entity)`). Довелося б робити небезпечне явне приведення типів `((BaseEntity) entity).getId()`, що порушувало б типову безпеку під час компіляції.

4. **`InMemoryCrudService<T extends BaseEntity>`** (`service/InMemoryCrudService.java`):
   - Зберігає дані у колекції `LinkedHashMap<Long, T>`.
   - Метод `save` використовує `put(entity.getId(), entity)`: якщо запис новий — додає, якщо id вже існує — оновлює значення, тому загальна кількість записів не збільшується.
   - Метод `delete` використовує `storage.remove(id) != null` (повертає `true`, якщо елемент видалено, і `false`, якщо його не було).
   - Працює як для `Product`, так і для `Customer` без будь-яких змін коду.

5. **Чому `findById` кидає виняток, а `delete` повертає `boolean`?**
   - **Для `findById` результат є обов'язковим:** клієнтський код очікує отримати конкретну сутність для подальших дій. Якщо сутності з таким `id` немає — сервіс не може повернути потрібне значення (а повернення `null` веде до ризику `NullPointerException`). Тому це виняткова ситуація, що сигналізується через `EntityNotFoundException`.
   - **Для `delete` ситуація «не було що видаляти» — звичайний результат, а не помилка:** мета операції — гарантувати відсутність сутності в системі. Якщо її там і не було, стан системи не порушено, система працює штатно. Метод просто повертає `boolean` (`true` — видалено, `false` — сутності з таким `id` і не було).

---

## Рівень 2. Середній

### 1. Comparator, а не Comparable

Для сортування обрано **`Comparator`**, а не `Comparable`:
* `Comparable` задає один «природний» порядок усередині класу, а нам потрібно два: за назвою і за назвою + ціною.
* До того ж `BaseEntity` спільний для `Product` і `Customer`, і «природний порядок» за назвою нав'язувати їм обом не варто.
* `Comparator` можна скласти через `thenComparing`, не чіпаючи самих сутностей.

### 2. Таблиця колекцій для сховища

(де n — кількість записів у сховищі):

| Операція | ArrayList | HashMap | LinkedHashMap (Обрано) | TreeMap |
| --- | --- | --- | --- | --- |
| **`findById`** | O(n) | O(1) серед. | **O(1) серед.** | O(log n) |
| **`save` (додати/оновити)** | O(n) | O(1) серед. | **O(1) серед.** | O(log n) |
| **`delete`** | O(n) | O(1) серед. | **O(1) серед.** | O(log n) |
| **Відсортований список за назвою** | O(n log n) | O(n log n) | **O(n log n)** | O(n log n): впорядкований за `id`, а не за назвою |
| **Порядок обходу (`findAll`)** | вставки | не гарантований | **вставки** | за ключем |

**Обґрунтування вибору:**  
Обрано **`LinkedHashMap`**: швидкі операції за `id` (O(1) у середньому), як у `HashMap`, плюс передбачуваний порядок вставки. `TreeMap` дає автоматичну впорядкованість за числовим ключем `id`, але для сортування за назвою чи ціною вона нічим не допомагає, а лише сповільнює базові операції до O(log n).

### 3. Узгодженість з `equals`

Компаратор **не узгоджений** з `equals`:
* Дві «Футболки» з різними `id` (`id=1` та `id=3`) дають `compare == 0` за назвою, але не `equals`, бо `equals` у `BaseEntity` порівнює сутності за їхнім унікальним `id`.
* **Наслідок:** у `TreeSet`/`TreeMap` з таким компаратором одна з них була б втрачена, тому для сховища він не підходить. А `List.sort` нічого не губить, оскільки список дозволяє зберігати кілька елементів з однаковими полями.

### 4. Чому метод `findAllSortedByNameAndByPrice()` винесено в `ProductService`?

Метод вторинного сортування залежить від поля ціни (`price`), яке є лише в товарах (`Product`). Товари з однаковою назвою (як дві футболки) в інтернет-магазині логічно розрізняти за ціною, щоб покупець міг порівняти варіанти від дешевших до дорожчих.

У базовому класі `BaseEntity` є лише `id` та `name`, а в сутності `Customer` є телефон, але немає ціни. Якби ми додали цей метод у загальний `CRUDService<T>`, сутність `Customer` мусила б реалізовувати непотрібний їй метод. Окремий інтерфейс `ProductService extends CRUDService<Product>` гарантує, що метод є тільки там, де він справді має сенс.

### 5. Незмінність початкового сховища

Методи сортування не змінюють початкове сховище:
```java
List<Product> sorted = findAll(); // створюється нова копія
sorted.sort(Comparator.comparing(Product::getName)
        .thenComparingDouble(Product::getPrice));
return sorted;
```
Сортується повернута копія списку, тому склад та порядок елементів у `storage` залишаються незмінними (що підтверджує вивід до й після сортувань у `CrudDemo`).

---

## Рівень 3. Високий

### 1. Узагальнений метод `findAllSorted` та `default`-метод

В інтерфейс `CRUDService<T>` додано узагальнений метод:
```java
List<T> findAllSorted(Comparator<? super T> comparator);
```
Через нього реалізовано обидва методи сортування:
1. `findAllSortedByName()` зроблено `default`-методом інтерфейсу `CRUDService<T>`:
   ```java
   default List<T> findAllSortedByName() {
       return findAllSorted(Comparator.comparing(BaseEntity::getName));
   }
   ```
2. `findAllSortedByNameAndByPrice()` в `InMemoryProductService` також делегує виконання методу `findAllSorted`:
   ```java
   @Override
   public List<Product> findAllSortedByNameAndByPrice() {
       return findAllSorted(Comparator.comparing(Product::getName)
               .thenComparingDouble(Product::getPrice));
   }
   ```

### 2. Чому `Comparator<? super T>`, а не `Comparator<T>`?

Компаратор лише приймає об'єкти для їх порівняння (споживає їх):
* Вираз `Comparator.comparing(BaseEntity::getName)` повертає `Comparator<BaseEntity>`.
* Оскільки дженерики в Java є інваріантними, `Comparator<BaseEntity>` не є підтипом `Comparator<Product>`. Якби сигнатура вимагала строго `Comparator<T>`, ми не змогли б передати компаратор базового класу `BaseEntity` у метод для `ProductService` або `CustomerService`.
* Запис `Comparator<? super T>` дозволяє передавати компаратор як для самого типу `T`, так і для будь-якого його батьківського класу (`BaseEntity`), що забезпечує зручне повторне використання коду.

### 3. Утилітний метод `printAll` та Wildcard `? extends BaseEntity`

Утилітний метод реалізовано в класі `EntityUtils`:
```java
public static void printAll(Collection<? extends BaseEntity> items) {
    if (items == null || items.isEmpty()) {
        return;
    }
    for (BaseEntity item : items) {
        System.out.println("  " + item);
    }
}
```

**Чому `List<Product>` не можна передати туди, де очікується `List<BaseEntity>`:**
* Дженерики в Java інваріантні: хоча `Product extends BaseEntity`, тип `List<Product>` НЕ є нащадком `List<BaseEntity>`.
* Якби компілятор дозволив таку передачу, виникла б проблема типобезпеки: через посилання `List<BaseEntity>` у список можна було б додати інший підтип, наприклад `new Customer(...)` (бо `Customer` теж `BaseEntity`), що призвело б до падіння вихідного списку товарів з `ClassCastException` під час виконання.
* **Що змінює wildcard `? extends BaseEntity`:** конструкція `Collection<? extends BaseEntity>` дозволяє методу приймати списки будь-яких підкласів `BaseEntity` (`List<Product>`, `List<Customer>`, `Set<Clothing>`). При цьому компілятор блокує будь-які спроби додавання нових елементів у таку колекцію всередині методу, дозволяючи лише безпечно читати елементи як `BaseEntity`.

---

### Примітка про сортування та рядки

* `BaseEntity` забороняє `null` або порожній рядок у конструкторі (`name == null || name.trim().isEmpty()`), тому в сховищі об'єктів з `null` не буде, і `NullPointerException` при порівнянні імен не виникає.
* Стандартний `String.compareTo` порівнює символи за їхніми кодами Unicode. Через це українські літери `І`, `Ї`, `Є`, `Ґ` стають поза алфавітним порядком (після `Я` або перед `А`). Для суворого алфавітного сортування локалізованих текстів у Java використовується `java.text.Collator.getInstance(new Locale("uk", "UA"))`. На тестових даних це не проявляється, оскільки імена починаються з загальних літер, але це стандартна особливість порівняння рядків у Java.

---

### Запуск програми:
```bash
# Компіляція
javac -d bin -encoding UTF-8 src/*.java src/delivery/*.java src/discount/*.java src/entity/*.java src/exception/*.java src/observer/*.java src/product/*.java src/service/*.java

# Запуск демонстрації
java -cp bin CrudDemo
```

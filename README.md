# BigFileReader

Задача была следующая:    
Напишите построчную сортировку большого текстового файла, не влезающего в оперативную память.
Размер требуемой памяти не должен зависеть от размера файла.
Длина строки разумная, одна строка сильно меньше, чем объем памяти.
Для проверки работоспособности нужен генератор таких файлов, принимающий в качестве параметров количество строк и их максимальную длину.

### Пример работы сортировки файла.

Строки в файле до сортировки:    
    
bcddd36352a    
abcdd33562f    
fgyyyz378     
fghhhh456
    
После сортировки:
    
abcdd33562f    
bcddd36352a    
fghhhh456    
fgyyyz378    
__________________________________

За образец строки взял данные из примера: латинский язык, числа, без пробелов, нижний регистр(регистр просто не
учитывал, но сохранил оба). Сортировка за счет "дробления" основного файла на части, допустимые к сортировке, путем
группировки строк по первым символам. Как только файл становится допустимого размера - использую сортировку из
Collections(слиянием). По итогу формирую отсортированный файл. После каждого "дробления" исходный файл удаляется, для
экономии места.

Перед запуском программы в application.yaml нужно:
-  Ввести название файла, в который будут сохранены строки(по умолчанию fileName: './src/main/file.txt'). 
-  Указать количество генерируемых строк(по умолчанию line-limit: 50000). 
-  Указать максимальную длину строки(по умолчанию max-line-length: 300).
-  Указать стартовое количество символов для сортировки(по умолчанию sort-size: 3).
-  Указать допустимую заполненность файла(в строках)(по умолчанию line-limit: 1000).
-  Указать путь для хранения вспомогательных файлов(по умолчанию helpers-file-path: './src/main/resources/helpers/')

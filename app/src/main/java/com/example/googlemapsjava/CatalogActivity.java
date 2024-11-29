package com.example.googlemapsjava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CatalogActivity extends AppCompatActivity {
    private ListView listView;
    private PlaceAdapter placeAdapter;
    private List<ObjectInfo> places;
    private List<ObjectInfo> filteredPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        listView = findViewById(R.id.listView);
        places = new ArrayList<>();
        filteredPlaces = new ArrayList<>();

        fillPlaces();
        filteredPlaces.addAll(places); // Изначально показываем все места

        placeAdapter = new PlaceAdapter(this, filteredPlaces);
        listView.setAdapter(placeAdapter);

        Button sortButton = findViewById(R.id.buttonSort);
        RadioGroup radioGroupCategories = findViewById(R.id.radioGroupCategories);

        radioGroupCategories.setOnCheckedChangeListener((group, checkedId) -> {
            String category = "all";

            if (checkedId == R.id.radioSight) {
                category = "sight";
            } else if (checkedId == R.id.radioRestaurant) {
                category = "restaurant";
            } else if (checkedId == R.id.radioHostel) {
                category = "hostel";
            } else if (checkedId == R.id.radioAll) {
                category = "all";
            }

            onCategorySelected(category);
        });

        sortButton.setOnClickListener(v -> {
            Collections.sort(filteredPlaces, Comparator.comparing(ObjectInfo::getTitle, String.CASE_INSENSITIVE_ORDER));
            placeAdapter.updateData(new ArrayList<>(filteredPlaces)); // Передаем копию списка
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            ObjectInfo selectedPlace = filteredPlaces.get(position);

            Intent intent = new Intent(CatalogActivity.this, ObjectInfoActivity.class);
            intent.putExtra("title", selectedPlace.getTitle());
            intent.putExtra("workingHours", selectedPlace.getWorkingHours());
            intent.putExtra("details", selectedPlace.getDetails());
            intent.putExtra("imageResourceId", selectedPlace.getImageResourceId());
            intent.putExtra("latitude", selectedPlace.getPosition().latitude); // Используем широту из position
            intent.putExtra("longitude", selectedPlace.getPosition().longitude); // Позиция для карты
            intent.putExtra("type", selectedPlace.getType()); // Передаем тип объекта
            startActivity(intent);
        });
    }

    private void fillPlaces() {
        places.add(new ObjectInfo("Софийский собор", "9:00 - 18:00", "Софийский собор – жемчужина белорусской архитектуры и один из древнейших храмов Восточной Европы. Построенный в XI веке, он удивляет своими масштабами и великолепием. Внутри вас ждут экспозиции исторического музея и органные концерты, которые погружают в атмосферу духовной красоты.", R.drawable.sophisky, new LatLng(55.486222, 28.758668), "sight"));
        places.add(new ObjectInfo("Борисов камень", "9:00 - 17:00", "Борисов камень – уникальный исторический памятник, связанный с эпохой князя Бориса Всеславича. Камень с высеченными крестом и надписью хранит тайны древних времен. Расположен у живописной реки Полоты, это место завораживает своей тишиной и глубиной.", R.drawable.borisov_kamen, new LatLng(55.48583, 28.75806), "sight"));
        places.add(new ObjectInfo("Краеведческий музей", "10:00 - 18:00", "Краеведческий музей – это путешествие через века. Здесь можно узнать о богатой истории города, начиная от древних времен и заканчивая современностью. Коллекция включает артефакты, оружие, документы и уникальные экспонаты, рассказывающие о жизни полочан.", R.drawable.kraeved_museum, new LatLng(55.485188, 28.762897), "sight"));
        places.add(new ObjectInfo("Музей белорусского книгопечатания", "Закрыто по понедельникам", "Музей белорусского книгопечатания посвящен выдающимся достижениям Франциска Скорины и истории письменности. Здесь можно увидеть редчайшие экземпляры книг, печатные станки, а также узнать о том, как зарождалась и развивалась белорусская культура через слово.", R.drawable.book_printing_museum, new LatLng(55.483964, 28.768215), "sight"));
        places.add(new ObjectInfo("Детский музей", "10:00 - 18:00", "Детский музей – это удивительное место, где история и наука оживают в интерактивной форме. Экспозиции созданы специально для юных исследователей, позволяя им узнать о природе, технике и культуре через увлекательные игры и мастер-классы.", R.drawable.child_museum, new LatLng(55.483061, 28.778797), "sight"));
        places.add(new ObjectInfo("Симеон Полоцкий", "10:00 - 17:00", "Симеон Полоцкий – выдающийся деятель белорусской культуры XVII века, поэт, философ и просветитель. Его творчество сыграло важную роль в развитии славянской литературы и образования. Экспозиция, посвященная Симеону, расскажет о его жизни и вкладе в историю.", R.drawable.simeon_polotskiy, new LatLng(55.485964, 28.774134), "sight"));
        places.add(new ObjectInfo("площадь Франциска Скорины", "Доступна круглосуточно", "Площадь Франциска Скорины – сердце Полоцка, где древняя история переплетается с современностью. Здесь установлен памятник первопечатнику, а сама площадь является популярным местом для прогулок и культурных мероприятий.", R.drawable.francisk_skoryna_square, new LatLng(55.484132, 28.778033), "sight"));
        places.add(new ObjectInfo("площадь Свободы", "Доступна круглосуточно", "Площадь Свободы – символический центр Полоцка, где каждый уголок дышит историей. Окруженная старинными зданиями, она часто становится местом проведения городских праздников и ярмарок.", R.drawable.svobody_square, new LatLng(55.486040, 28.767747), "sight"));
        places.add(new ObjectInfo("Полоцкий государственный университет", "9:00 - 18:00", "Полоцкий государственный университет – ведущий образовательный и культурный центр региона. Его история связана с традициями полоцкой просветительской школы. Кампус университета удивляет сочетанием современной архитектуры и исторических зданий.", R.drawable.poloct_university, new LatLng(55.485948, 28.763741), "sight"));
        places.add(new ObjectInfo("Художественная галерея", "10:00 - 19:00", "Художественная галерея Полоцка – это собрание уникальных произведений искусства, от классической живописи до современных экспозиций. Посетители могут насладиться творениями белорусских и зарубежных мастеров, а также тематическими выставками.", R.drawable.art_gallery, new LatLng(55.485994, 28.763831), "sight"));
        places.add(new ObjectInfo("Музей традиционного ручного ткачества Поозерья", "10:00 - 18:00", "Музей традиционного ручного ткачества Полоцка – это место, где можно познакомиться с древним ремеслом белорусского народа. Экспозиции включают редкие образцы текстиля, традиционные инструменты и мастер-классы по ткачеству.", R.drawable.traditional_weaving_museum, new LatLng(55.488014, 28.768206), "sight"));
        places.add(new ObjectInfo("Природно-экологический музей", "10:00 - 18:00", "Природно-экологический музей приглашает гостей в мир природы. Здесь можно узнать о флоре и фауне региона, экологических проблемах и способах их решения. Это место вдохновляет на заботу о природе и окружающей среде.", R.drawable.nature_eco_museum, new LatLng(55.488835, 28.769733), "sight"));
        places.add(new ObjectInfo("Галерея мастеров", "10:00 - 18:00", "Галерея мастеров – творческое пространство, где представлены работы местных художников и ремесленников. От живописи до керамики – здесь можно увидеть уникальные изделия, отражающие культуру и традиции региона.", R.drawable.master_gallery, new LatLng(55.485734, 28.771458), "sight"));

        // Рестораны
        places.add(new ObjectInfo("Суши Шоп", "10:00 - 22:00", "Суши Шоп – уютное место, где можно насладиться вкусом традиционной японской кухни. В меню представлены разнообразные роллы, суши, сеты и закуски, приготовленные из свежих и качественных ингредиентов. Идеально подходит для обеда, ужина или перекуса на вынос.", R.drawable.sushi_shop, new LatLng(55.494441, 28.775716), "restaurant"));
        places.add(new ObjectInfo("Теремок", "10:00 - 20:00", "Теремок – семейное кафе с атмосферой уюта и тепла. Здесь подают блюда домашней белорусской кухни, включая ароматные драники, аппетитные супы и десерты. Отличное место для отдыха с семьей или друзьями.", R.drawable.teremok, new LatLng(55.488188, 28.779785), "restaurant"));
        places.add(new ObjectInfo("Росквіт", "11:00 - 23:00", "Росквіт – ресторан, в котором белорусские традиции встречаются с современными кулинарными трендами. Вас ждут блюда из натуральных продуктов, гостеприимная атмосфера и широкий выбор напитков. Подходит как для дружеских встреч, так и для особенных событий.", R.drawable.roskvit, new LatLng(55.491579, 28.781348), "restaurant"));
        places.add(new ObjectInfo("Верхний замок", "09:00 - 21:00", "Верхний замок – это атмосферный ресторан, вдохновленный историей древнего Полоцка. Интерьер с элементами старинной архитектуры и блюда, приготовленные по старинным рецептам, переносят гостей в эпоху средневековья.", R.drawable.upper_castle, new LatLng(55.486229, 28.766580), "restaurant"));
        places.add(new ObjectInfo("Grand Pub", "11:00 - 00:00", "Grand Pub – стильное место для любителей живой музыки, вкусной еды и хорошей компании. В меню представлены закуски, горячие блюда и широкий ассортимент напитков. Отличный выбор для тех, кто хочет расслабиться в непринужденной атмосфере.", R.drawable.grand_pub, new LatLng(55.485622, 28.770218), "restaurant"));
        places.add(new ObjectInfo("Чабор", "10:00 - 22:00", "Чабор – это кафе, где царит уют и радушие. В меню представлены традиционные белорусские блюда и напитки, приготовленные с любовью. Идеальное место для тех, кто хочет насладиться вкусами национальной кухни.", R.drawable.chabor, new LatLng(55.486413, 28.773119), "restaurant"));
        places.add(new ObjectInfo("Гурман", "09:00 - 21:00", "Гурман – это ресторан для истинных ценителей изысканных вкусов. Здесь можно попробовать блюда европейской и белорусской кухни, приготовленные талантливыми шеф-поварами. Подходит как для деловых встреч, так и для романтических ужинов.", R.drawable.gurman, new LatLng(55.485255, 28.774871), "restaurant"));

        // Хостелы
        places.add(new ObjectInfo("Славянский", "Круглосуточно", "Гостиница Славянский – это сочетание современного комфорта и традиционного белорусского гостеприимства. Расположенная в самом центре Полоцка, она идеально подходит для путешественников, ценящих уют, удобное расположение и высокий уровень сервиса.", R.drawable.slavyanskiy, new LatLng(55.485188, 28.777476), "hostel"));
        places.add(new ObjectInfo("Парус", "Круглосуточно", "Ресторан Парус – место, где гастрономические традиции встречаются с современными кулинарными решениями. Уютная атмосфера, изысканные блюда и потрясающий вид на реку делают его отличным выбором для обедов, ужинов и особых событий.", R.drawable.parus, new LatLng(55.482622, 28.781806), "hostel"));
        places.add(new ObjectInfo("София Хостел", "Круглосуточно", "София Хостел – уютное и доступное жилье для туристов, расположенное вблизи главных достопримечательностей города. Чистые номера, дружественная атмосфера и комфорт делают его идеальным местом для бюджетного отдыха в Полоцке.", R.drawable.sofia_hostel, new LatLng(55.509982, 28.766930), "hostel"));
        places.add(new ObjectInfo("Гостиный двор", "Круглосуточно", "Гостиный двор – исторически значимое место, которое сочетает в себе дух старины и современные удобства. Здесь можно не только остановиться на ночлег, но и почувствовать атмосферу традиционного полоцкого уклада жизни.", R.drawable.gostiny_dvor, new LatLng(55.482077, 28.731123), "hostel"));
    }

    private void onCategorySelected(String category) {
        filteredPlaces.clear();

        if (category.equals("all")) {
            filteredPlaces.addAll(places);
        } else {
            for (ObjectInfo place : places) {
                if (place.getType().trim().equalsIgnoreCase(category.trim())) {
                    filteredPlaces.add(place);
                }
            }
        }

        if (filteredPlaces.isEmpty()) {
            Toast.makeText(this, "Нет мест для выбранной категории", Toast.LENGTH_SHORT).show();
        }

        placeAdapter.updateData(new ArrayList<>(filteredPlaces)); // Передаем копию списка
    }
}

package com.example.googlemapsjava;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.codebyashish.googledirectionapi.AbstractRouting;
import com.codebyashish.googledirectionapi.ErrorHandling;
import com.codebyashish.googledirectionapi.RouteDrawing;
import com.codebyashish.googledirectionapi.RouteInfoModel;
import com.codebyashish.googledirectionapi.RouteListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.googlemapsjava.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.widget.RadioGroup;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, RouteListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private Button currentLocationButton;
    private static final int REQUEST_LOCATION_PERMISSION = 101;

    private HashMap<Marker, ObjectInfo> markerDataMap = new HashMap<>();

    private ArrayList<ObjectInfo> visitedPlaces = new ArrayList<>();

    private LatLng currentLocation;
    private LatLng destinationLocation;
    private Button routeButton;
    private ArrayList<Polyline> polylines = new ArrayList<>();

    private Marker currentLocationMarker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        currentLocationButton = findViewById(R.id.current_location_button);
        currentLocationButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
                getCurrentLocation();
            }
        });

        Button buttonAccount = findViewById(R.id.buttonAccount);
        buttonAccount.setOnClickListener(v -> {
            Intent intent = new Intent(MapsActivity.this, AccountActivity.class);
            startActivity(intent);
        });

        Button exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(v -> {
            finishAffinity(); // Закрывает текущее активити и все его родительские активити
        });

        routeButton = findViewById(R.id.button_route);
        routeButton.setOnClickListener(v -> {
            if (currentLocation != null && destinationLocation != null) {
                getRoutePoints(currentLocation, destinationLocation);
            } else {
                Toast.makeText(this, "Не удалось получить текущую локацию или цель маршрута", Toast.LENGTH_SHORT).show();
            }
        });


        // Настройка радиокнопок
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Очищаем все маркеры при каждом изменении
            clearObjectMarkers();

            // Проверяем, какая радиокнопка выбрана
            if (checkedId == R.id.radioSights) {
                addSightsMarkers(); // Добавляем маркеры достопримечательностей
            } else if (checkedId == R.id.radioRestaurants) {
                addRestaurantsMarkers(); // Добавляем маркеры ресторанов
            } else if (checkedId == R.id.radioHostels) {
                addHostelsMarkers(); // Добавляем маркеры хостелов
            } else if (checkedId == R.id.radioNone) {
                // Ничего не добавляется, карта остается пустой
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap; // Сохраняем ссылку на объект GoogleMap

        // Получаем координаты из интента
        Intent intent1 = getIntent();
        double latitude = intent1.getDoubleExtra("latitude", 0.0);
        double longitude = intent1.getDoubleExtra("longitude", 0.0);

        // Проверка, что координаты не равны нулю
        if (latitude != 0.0 && longitude != 0.0) {
            // Устанавливаем маркер на карте
            LatLng location = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(location).title("Выбранное место"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15)); // Приближение к маркеру
        }

        mMap.setOnMarkerClickListener(marker -> {
            destinationLocation = marker.getPosition();
            marker.showInfoWindow();
            return true;
        });

        // Обработчик долгого нажатия на информационное окно
        mMap.setOnInfoWindowLongClickListener(marker -> {
            ObjectInfo info = markerDataMap.get(marker);
            if (info != null) {
                // Добавляем место в VisitedPlacesManager
                VisitedPlacesManager.getInstance().addPlace(info);

                // Показываем уведомление
                Toast.makeText(this, info.getTitle() + " добавлено в посещенные места", Toast.LENGTH_SHORT).show();

                // Переход на экран с информацией о месте
                Intent intent = new Intent(MapsActivity.this, ObjectInfoActivity.class);
                intent.putExtra("title", info.getTitle());
                intent.putExtra("workingHours", info.getWorkingHours());
                intent.putExtra("details", info.getDetails());
                intent.putExtra("imageResourceId", info.getImageResourceId());

                // Добавляем широту и долготу маркера
                LatLng position = marker.getPosition();
                intent.putExtra("latitude", position.latitude);
                intent.putExtra("longitude", position.longitude);

                startActivity(intent);
            }
        });
    }


    private void addSightsMarkers() {
        List<LatLng> sights = Arrays.asList(
                new LatLng(55.486222, 28.758668), // Софийский собор
                new LatLng(55.48583, 28.75806), // Борисов камень
                new LatLng(55.485188, 28.762897), // Краеведческий Музей
                new LatLng(55.483964, 28.768215), // Музей беларусского книгопечатания
                new LatLng(55.483061, 28.778797), // Детский музей
                new LatLng(55.485964, 28.774134), // Симеон Полоцкий
                new LatLng(55.484132, 28.778033), // площадь Франциска Скорины
                new LatLng(55.486040, 28.767747), // площадь Свободы
                new LatLng(55.485948, 28.763741), // Полоцкий государственный университет
                new LatLng(55.485994, 28.763831), // Художественная галерея
                new LatLng(55.488014, 28.768206), // Музей традиционного ручного ткачества Поозерья
                new LatLng(55.488835, 28.769733), // Природно-экологический музей
                new LatLng(55.485734, 28.771458)  // Галерея мастеров
        );

        String[] sightsNames = new String[]{
                "Софийский собор",
                "Борисов камень",
                "Краеведческий Музей",
                "Музей беларусского книгопечатания",
                "Детский музей",
                "Симеон Полоцкий",
                "площадь Франциска Скорины",
                "площадь Свободы",
                "Полоцкий государственный университет",
                "Художественная галерея",
                "Музей традиционного ручного ткачества Поозерья",
                "Природно-экологический музей",
                "Галерея мастеров"
        };

        String[] workingHours = new String[]{
                "9:00 - 18:00",
                "9:00 - 17:00",
                "10:00 - 18:00",
                "Закрыто по понедельникам",
                "10:00 - 18:00",
                "10:00 - 17:00",
                "Доступна круглосуточно",
                "Доступна круглосуточно",
                "9:00 - 18:00",
                "10:00 - 19:00",
                "10:00 - 18:00",
                "10:00 - 18:00",
                "10:00 - 18:00"
        };

        String[] details = new String[]{
                "Софийский собор – жемчужина белорусской архитектуры и один из древнейших храмов Восточной Европы. Построенный в XI веке, он удивляет своими масштабами и великолепием. Внутри вас ждут экспозиции исторического музея и органные концерты, которые погружают в атмосферу духовной красоты.",
                "Борисов камень – уникальный исторический памятник, связанный с эпохой князя Бориса Всеславича. Камень с высеченными крестом и надписью хранит тайны древних времен. Расположен у живописной реки Полоты, это место завораживает своей тишиной и глубиной.",
                "Краеведческий музей – это путешествие через века. Здесь можно узнать о богатой истории города, начиная от древних времен и заканчивая современностью. Коллекция включает артефакты, оружие, документы и уникальные экспонаты, рассказывающие о жизни полочан.",
                "Музей белорусского книгопечатания посвящен выдающимся достижениям Франциска Скорины и истории письменности. Здесь можно увидеть редчайшие экземпляры книг, печатные станки, а также узнать о том, как зарождалась и развивалась белорусская культура через слово.",
                "Детский музей – это удивительное место, где история и наука оживают в интерактивной форме. Экспозиции созданы специально для юных исследователей, позволяя им узнать о природе, технике и культуре через увлекательные игры и мастер-классы.",
                "Симеон Полоцкий – выдающийся деятель белорусской культуры XVII века, поэт, философ и просветитель. Его творчество сыграло важную роль в развитии славянской литературы и образования. Экспозиция, посвященная Симеону, расскажет о его жизни и вкладе в историю.",
                "Площадь Франциска Скорины – сердце Полоцка, где древняя история переплетается с современностью. Здесь установлен памятник первопечатнику, а сама площадь является популярным местом для прогулок и культурных мероприятий.",
                "Площадь Свободы – символический центр Полоцка, где каждый уголок дышит историей. Окруженная старинными зданиями, она часто становится местом проведения городских праздников и ярмарок.",
                "Полоцкий государственный университет – ведущий образовательный и культурный центр региона. Его история связана с традициями полоцкой просветительской школы. Кампус университета удивляет сочетанием современной архитектуры и исторических зданий.",
                "Художественная галерея Полоцка – это собрание уникальных произведений искусства, от классической живописи до современных экспозиций. Посетители могут насладиться творениями белорусских и зарубежных мастеров, а также тематическими выставками.",
                "Музей традиционного ручного ткачества Полоцка – это место, где можно познакомиться с древним ремеслом белорусского народа. Экспозиции включают редкие образцы текстиля, традиционные инструменты и мастер-классы по ткачеству.",
                "Природно-экологический музей приглашает гостей в мир природы Поозерья. Здесь можно узнать о флоре и фауне региона, экологических проблемах и способах их решения. Это место вдохновляет на заботу о природе и окружающей среде.",
                "Галерея мастеров – творческое пространство, где представлены работы местных художников и ремесленников. От живописи до керамики – здесь можно увидеть уникальные изделия, отражающие культуру и традиции региона."
        };

        int[] imageResourceIds = new int[]{
                R.drawable.sophisky,
                R.drawable.borisov_kamen,
                R.drawable.kraeved_museum,
                R.drawable.book_printing_museum,
                R.drawable.child_museum, // Детский музей
                R.drawable.simeon_polotskiy, // Симеон Полоцкий
                R.drawable.francisk_skoryna_square, // площадь Франциска Скорины
                R.drawable.svobody_square, // площадь Свободы
                R.drawable.poloct_university, // Полоцкий государственный университет
                R.drawable.art_gallery, // Художественная галерея
                R.drawable.traditional_weaving_museum, // Музей традиционного ручного ткачества Поозерья
                R.drawable.nature_eco_museum, // Природно-экологический музей
                R.drawable.master_gallery // Галерея мастеров
        };

        for (int i = 0; i < sights.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(sights.get(i))
                    .title(sightsNames[i])
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            Marker marker = mMap.addMarker(markerOptions);

            // Добавляем информацию в HashMap
            markerDataMap.put(marker, new ObjectInfo(sightsNames[i], workingHours[i], details[i], imageResourceIds[i], sights.get(i), "sights"));
        }
    }


    private void addRestaurantsMarkers() {
        List<LatLng> restaurants = Arrays.asList(
                new LatLng(55.494441, 28.775716), // «Суши Шоп»
                new LatLng(55.488188, 28.779785), // «Теремок»
                new LatLng(55.491579, 28.781348), // «Росквіт»
                new LatLng(55.486229, 28.766580), // «Верхний замок»
                new LatLng(55.485622, 28.770218), // Grand Pub
                new LatLng(55.486413, 28.773119), // Чабор
                new LatLng(55.485255, 28.774871)  // Гурман
        );

        String[] restaurantNames = new String[]{
                "Суши Шоп",
                "Теремок",
                "Росквіт",
                "Верхний замок",
                "Grand Pub",
                "Чабор",
                "Гурман"
        };

        String[] workingHours = new String[]{
                "10:00 - 22:00",
                "10:00 - 20:00",
                "11:00 - 23:00",
                "09:00 - 21:00",
                "11:00 - 00:00",
                "10:00 - 22:00",
                "09:00 - 21:00"
        };

        String[] details = new String[]{
                "Суши Шоп – уютное место, где можно насладиться вкусом традиционной японской кухни. В меню представлены разнообразные роллы, суши, сеты и закуски, приготовленные из свежих и качественных ингредиентов. Идеально подходит для обеда, ужина или перекуса на вынос",
                "Теремок – семейное кафе с атмосферой уюта и тепла. Здесь подают блюда домашней белорусской кухни, включая ароматные драники, аппетитные супы и десерты. Отличное место для отдыха с семьей или друзьями",
                "Росквіт – ресторан, в котором белорусские традиции встречаются с современными кулинарными трендами. Вас ждут блюда из натуральных продуктов, гостеприимная атмосфера и широкий выбор напитков. Подходит как для дружеских встреч, так и для особенных событий",
                "Верхний замок – это атмосферный ресторан, вдохновленный историей древнего Полоцка. Интерьер с элементами старинной архитектуры и блюда, приготовленные по старинным рецептам, переносят гостей в эпоху средневековья.",
                "Grand Pub – стильное место для любителей живой музыки, вкусной еды и хорошей компании. В меню представлены закуски, горячие блюда и широкий ассортимент напитков. Отличный выбор для тех, кто хочет расслабиться в непринужденной атмосфере.",
                "Чабор – это кафе, где царит уют и радушие. В меню представлены традиционные белорусские блюда и напитки, приготовленные с любовью. Идеальное место для тех, кто хочет насладиться вкусами национальной кухни.",
                "Гурман – это ресторан для истинных ценителей изысканных вкусов. Здесь можно попробовать блюда европейской и белорусской кухни, приготовленные талантливыми шеф-поварами. Подходит как для деловых встреч, так и для романтических ужинов."
        };

        int[] imageResourceIds = new int[]{
                R.drawable.sushi_shop,
                R.drawable.teremok,
                R.drawable.roskvit,
                R.drawable.upper_castle,
                R.drawable.grand_pub,
                R.drawable.chabor,
                R.drawable.gurman
        };

        for (int i = 0; i < restaurants.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(restaurants.get(i))
                    .title(restaurantNames[i])
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            Marker marker = mMap.addMarker(markerOptions);

            // Добавляем информацию в HashMap
            markerDataMap.put(marker, new ObjectInfo(restaurantNames[i], workingHours[i], details[i], imageResourceIds[i], restaurants.get(i), "restaraunts"));
        }
    }

    private void addHostelsMarkers() {
        List<LatLng> hostels = Arrays.asList(
                new LatLng(55.485188, 28.777476), // «Славянский»
                new LatLng(55.482622, 28.781806), // «Парус»
                new LatLng(55.509982, 28.766930), // «София Хостел»
                new LatLng(55.482077, 28.731123)  // «Гостиный двор»
        );

        String[] hostelNames = new String[]{
                "Славянский",
                "Парус",
                "София Хостел",
                "Гостиный двор"
        };

        String[] workingHours = new String[]{
                "Круглосуточно",
                "Круглосуточно",
                "Круглосуточно",
                "Круглосуточно"
        };

        String[] details = new String[]{
                "Гостиница Славянский – это сочетание современного комфорта и традиционного белорусского гостеприимства. Расположенная в самом центре Полоцка, она идеально подходит для путешественников, ценящих уют, удобное расположение и высокий уровень сервиса.",
                "Ресторан Парус – место, где гастрономические традиции встречаются с современными кулинарными решениями. Уютная атмосфера, изысканные блюда и потрясающий вид на реку делают его отличным выбором для обедов, ужинов и особых событий.",
                "София Хостел – уютное и доступное жилье для туристов, расположенное вблизи главных достопримечательностей города. Чистые номера, дружественная атмосфера и комфорт делают его идеальным местом для бюджетного отдыха в Полоцке.",
                "Гостиный двор – исторически значимое место, которое сочетает в себе дух старины и современные удобства. Здесь можно не только остановиться на ночлег, но и почувствовать атмосферу традиционного полоцкого уклада жизни."
        };

        int[] imageResourceIds = new int[]{
                R.drawable.slavyanskiy,
                R.drawable.parus,
                R.drawable.sofia_hostel,
                R.drawable.gostiny_dvor
        };

        for (int i = 0; i < hostels.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(hostels.get(i))
                    .title(hostelNames[i])
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

            Marker marker = mMap.addMarker(markerOptions);

            // Добавляем информацию в HashMap
            markerDataMap.put(marker, new ObjectInfo(hostelNames[i], workingHours[i], details[i], imageResourceIds[i], hostels.get(i), "hostels"));
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                // Проверка, если текущий маркер уже существует, то обновляем его позицию, а не создаем новый
                if (currentLocationMarker == null) {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(currentLocation)
                            .title("Вы здесь")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                            .anchor(0.5f, 0.5f);

                    currentLocationMarker = mMap.addMarker(markerOptions);
                } else {
                    currentLocationMarker.setPosition(currentLocation);
                }

                // Перемещаем камеру к текущему местоположению с увеличением
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            } else {
                Toast.makeText(this, "Не удалось определить текущее местоположение", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearObjectMarkers() {
        for (Marker marker : markerDataMap.keySet()) {
            marker.remove();
        }
        markerDataMap.clear();
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void getRoutePoints(LatLng start, LatLng end) {
        if (start == null || end == null) {
            Toast.makeText(this, "Unable to get location", Toast.LENGTH_LONG).show();
            Log.e("TAG", " latlngs are null");
        } else {
            RouteDrawing routeDrawing = new RouteDrawing.Builder()
                    .context(MapsActivity.this)
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(start, end)
                    .build();
            routeDrawing.execute();
        }
    }

    @Override
    public void onRouteSuccess(ArrayList<RouteInfoModel> routeInfoModelArrayList, int routeIndexing) {
        if (polylines != null) {
            for (Polyline polyline : polylines) {
                polyline.remove();
            }
            polylines.clear();
        }

        PolylineOptions polylineOptions = new PolylineOptions();
        for (int i = 0; i < routeInfoModelArrayList.size(); i++) {
            if (i == routeIndexing) {
                polylineOptions.color(Color.BLACK);
                polylineOptions.width(12);
                polylineOptions.addAll(routeInfoModelArrayList.get(routeIndexing).getPoints());
                polylineOptions.startCap(new RoundCap());
                polylineOptions.endCap(new RoundCap());
                Polyline polyline = mMap.addPolyline(polylineOptions);
                polylines.add(polyline);
            }
        }
    }



    @Override
    public void onRouteFailure(ErrorHandling e) {

    }

    @Override
    public void onRouteStart() {

    }

    @Override
    public void onRouteCancelled() {

    }
}
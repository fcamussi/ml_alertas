# ML Alertas

Aplicación de alertas para Mercado Libre para Android.

Permite agregar búsquedas para que las mismas se realicen de forma automáticas en segundo plano cada cierto intervalo de tiempo configurable y avise, mediante una notificación push, cuando se publica un árticulo nuevo que coincida con los criterios de la búsqueda.

Para las búsquedas se utiliza la API de Mercado Libre, mediante la librería [ML Searcher](https://github.com/fcamussi/ml_searcher) que desarrollé especialmente para éste proyecto, y permite hacer búsquedas en los diferentes sitios (países) de Mercado Libre.

Es una aplicación ideal para coleccionistas que buscan artículos dificil de encontrar y que se venden muy rápido.

## Capturas

![screenshot1](https://user-images.githubusercontent.com/75378876/189788294-cc9e93dd-8d57-4db5-b755-e168f13190f6.png)
![screenshot2](https://user-images.githubusercontent.com/75378876/189788301-acef15e3-491f-409c-b49d-d7835eb4c8ce.png)

![screenshot3](https://user-images.githubusercontent.com/75378876/189788310-1d74c010-7b43-4c54-909e-983426bcd75f.png)
![screenshot4](https://user-images.githubusercontent.com/75378876/189788336-f6c0b1f7-3112-4ab2-bc3c-1d4d1b57ff89.png)

![screenshot5](https://user-images.githubusercontent.com/75378876/189788357-82196b2b-e523-4307-a8ac-b48a81c52810.png)

## Características

- Opción de país para cada búsqueda
- Intervalos de tiempo configurable para cada búsqueda
- Notficación push cuando se encuentra un nuevo artículo
- Opción de no buscar en segundo plano si no se está conectado a una red Wi-Fi
- Opción de no buscar si la batería está baja
- Multilenguaje: Inglés, Español, Portugués

## Requisitos para la compilación
- Android Studio

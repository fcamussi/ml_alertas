# ML Alertas

Aplicación de alertas para Mercado Libre para Android.

Permite agregar búsquedas para que las mismas se realicen de forma automáticas en segundo plano cada cierto intervalo de tiempo configurable y avise, mediante una notificación push, cuando se publica un árticulo nuevo que coincida con los criterios de la búsqueda.

Para la búsqueda se utiliza la API de Mercado Libre, mediante la librería [ML Searcher](https://github.com/fcamussi/ml_searcher) que desarrollé especialmente para éste proyecto, y permite hacer búsquedas en los diferentes sitios (países) de Mercado Libre.

Es una aplicación ideal para coleccionistas que buscan artículos dificil de encontrar y que se venden muy rápido.

## Capturas

![screenshot1](https://user-images.githubusercontent.com/75378876/189466357-bd0f6e6c-347a-4a8d-8e32-77d21f587043.png)
![screenshot2](https://user-images.githubusercontent.com/75378876/189466358-6349f12f-751e-44d3-b198-4c73f04e90ac.png)

![screenshot3](https://user-images.githubusercontent.com/75378876/189466359-b039572a-962f-4e9c-8e80-f67b1026f363.png)
![screenshot4](https://user-images.githubusercontent.com/75378876/189504538-709373b5-0a76-46a2-8864-6c85c5a246b8.png)

![screenshot5](https://user-images.githubusercontent.com/75378876/189466360-9a4642c5-c8f2-494a-8c6b-ea33601046d5.png)

## Características

- Elección de país para cada búsqueda
- Intervalos de tiempo configurable para cada búsqueda
- Notficación push cuando se encuentra un nuevo artículo
- Opción de no buscar en segundo plano si no se está conectado a una red Wi-Fi
- Opción de no buscar si la batería está baja
- Multilenguaje: Inglés, Español, Portugués

## Requisitos para la compilación
- Android Studio

# ML Alertas

Aplicación de alertas para Mercado Libre para Android.

Permite agregar búsquedas para que las mismas se realicen de forma automáticas en segundo plano cada cierto intervalo de tiempo configurable y avise, mediante una notificación push, cuando se publica un árticulo nuevo que coincida con los criterios de la búsqueda.

Para las búsquedas se utiliza la API de Mercado Libre, mediante la librería [ML Searcher](https://github.com/fcamussi/ml_searcher) que desarrollé especialmente para éste proyecto, y permite hacer búsquedas en los diferentes sitios (países) de Mercado Libre.

Es una aplicación ideal para coleccionistas que buscan artículos dificil de encontrar y que se venden muy rápido.

## Capturas

![screenshot1](https://user-images.githubusercontent.com/75378876/190887590-30b7168e-6233-4434-9951-dcb017c3deb0.png)
![screenshot2](https://user-images.githubusercontent.com/75378876/190887591-46fc3e2e-392a-4a43-9d94-ff84e5acc456.png)

![screenshot3](https://user-images.githubusercontent.com/75378876/190887594-e8df084e-b5aa-44b7-8010-ea6da4c3950e.png)
![screenshot4](https://user-images.githubusercontent.com/75378876/190887595-7f36f374-e3a0-405e-8e78-91429aac1024.png)

![screenshot5](https://user-images.githubusercontent.com/75378876/190887596-5c09cb44-d699-44ee-ad29-c350c3d4b3bf.png)


## Características

- Opción de país para cada búsqueda
- Intervalos de tiempo configurable para cada búsqueda
- Notficación push cuando se encuentra un nuevo artículo
- Opción de no buscar en segundo plano si no se está conectado a una red Wi-Fi
- Opción de no buscar si la batería está baja
- Multilenguaje: Inglés, Español, Portugués

## Requisitos para la compilación
- Android Studio

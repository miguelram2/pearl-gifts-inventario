Pearl Gifts — Sistema de Control de Inventario



Sistema de escritorio para el control de inventario de Pearl Gifts, un negocio familiar (PYME) dedicado a la venta de cajas de regalo, arreglos y decoración.



Tabla de contenidos



\- \[Descripción](#descripción)

\- \[Problema identificado](#problema-identificado)

\- \[Solución](#solución)

\- \[Arquitectura](#arquitectura)

\- \[Requerimientos](#requerimientos)

\- \[Instalación](#instalación)

	- \[Ambiente de desarrollo](#ambiente-de-desarrollo)

	- \[Ejecutar pruebas manualmente](#ejecutar-pruebas-manualmente)

	- \[Implementación local (producción)](#implementación-local-producción)

\- \[Configuración](#configuración)

\- \[Uso](#uso)

	- \[Manual de usuario final](#manual-de-usuario-final)

	- \[Manual de usuario administrador](#manual-de-usuario-administrador)


\- \[Roadmap](#roadmap)

\- \[Producto](#producto)



Descripción



Pearl Gifts es administrado por dos personas: Perla Montiel (creadora y administradora) y Tania Torres (apoyo en envíos y productos). El negocio no cuenta con área de TI, por lo que el control de inventario se ha manejado de forma manual.



Problema identificado



El inventario se lleva de forma mixta y no centralizada (Excel y libreta física), sin una fuente única de verdad. Esto genera falta de visibilidad del stock real, inconsistencias entre ambas fuentes, dificultad para planear compras y pérdida de tiempo al conciliar información.



Solución



Aplicación de escritorio en Java que centraliza el registro de productos, entradas, salidas y consultas de inventario en tiempo real, con roles diferenciados para Perla (administradora) y Tania (operativa), respaldada por una base de datos MySQL local.



Arquitectura



La solución sigue una arquitectura en capas, ejecutándose por completo en la computadora del negocio (sin servidor remoto), imagen adjuntada en fase III



Requerimientos



| Tipo | Detalle |

|---|---|

| Sistema operativo | Windows (equipo actual del negocio) |

| Servidor de aplicación | No aplica — aplicación de escritorio, no requiere servidor de aplicaciones |

| Servidor de base de datos | MySQL Server 8.0 (instalación local) |

| Lenguaje / SDK | JDK 21 (OpenJDK) |

| Driver de base de datos | MySQL Connector/J (JDBC) |

| Pruebas | JUnit 4.13.2 + Hamcrest Core 1.3 |

| Herramienta de administración de BD | MySQL Workbench 8.0 CE (opcional, uso del desarrollador) |

| Integración continua | GitHub Actions |

| Administración de proyecto | Zube |



\## Instalación



Ambiente de desarrollo



1\. Instalar \[JDK 21](https://adoptium.net/) y verificar con `java -version`.

2\. Instalar \[MySQL Server 8.0](https://dev.mysql.com/downloads/mysql/) y, opcionalmente, MySQL Workbench 8.0 CE.

3\. Clonar el repositorio:



git clone https://github.com/miguelram2/pearl-gifts-inventario.git

cd pearl-gifts-inventario



4\. Descargar las dependencias (JUnit y el driver JDBC) en la carpeta `lib/` — ver sección \[Configuración](#configuración).



Ejecutar pruebas manualmente


javac -cp "lib/\*" -d out $(find src -name "\*.java")

java -cp "out;lib/\*" org.junit.runner.JUnitCore com.pearlgifts.inventario.CategoriaTest




Implementación local (producción)



1\. Compilar el proyecto completo y generar el `.jar` ejecutable (ver sección \[Producto](#producto)).

2\. Crear la base de datos en el MySQL local usando el script `docs/database/schema.sql`.

3\. Ajustar el archivo de configuración con los datos de conexión (ver siguiente sección).

4\. Ejecutar la aplicación:

java -jar pearl-gifts-inventario.jar



Configuración



La conexión a la base de datos se define en `config.properties` (raíz del proyecto):



properties

db.url=jdbc:mysql://localhost:3306/pearl\_gifts\_inventario

db.user=root

db.password=Moga1810




Antes del primer uso, ejecutar el script `docs/database/schema.sql` en MySQL Workbench (o por línea de comandos) para crear las tablas necesarias (productos, categorías, movimientos, usuarios).



Uso


	Manual de usuario final


Debe cubrir: iniciar sesión, consultar existencias, registrar una entrada/salida y ver alertas de stock bajo — pensado para el uso operativo de Tania.




Roadmap



Funcionalidades identificadas durante el análisis de requisitos, pero que quedan fuera del alcance de esta primera versión:



- Carga masiva de productos por archivo CSV.

- Código de barras / escaneo de productos.

- Historial de cambios (versionado de ediciones a productos).

- Notificaciones automáticas de stock bajo por correo o WhatsApp.

- Gráficas y dashboards visuales de reportes.

- Proyecciones automáticas de demanda.

- Roles personalizados y permisos granulares por módulo.

- Recuperación de contraseña y autenticación multifactor.

- Panel de auditoría con filtros avanzados.

- Acceso remoto o desde dispositivos móviles.



Producto


\- \*\*Código fuente\*\*: disponible en este repositorio, branch `master` (versión GA) y `develop` (última versión en desarrollo).

\- \*\*Ejecutable\*\*: `pearl-gifts-inventario.jar`, disponible en la sección \[Releases](../../releases) del repositorio.

\- \*\*Demo en video\*\*: 


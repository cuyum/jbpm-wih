#JBPM-WIH
WorkItem Handlers para jBPM6

##1. Contenido
1. **whi-rest** : WorkItem Handler para invocación servicios REST con entrada y salida JSON. El JSON de entrada es generado a partir del `java.util.Map<String, Object>` de parametros pasados por jBPM al WorkItem Handler, mientras que el JSON resultante de la llamada al servico REST es convertido nuevamente a `java.util.Map<String, Object>` para devolver al jBPM.

##2. Requisitos
###Para construcción
1. Instalado Java Oracle JDK 6.x o 7.x
2. Instalado Maven 3.0.4 o superior
3. Instalado cliente git

###Para depliegue y ejecución
1. Instalado RedHat JBPM Suite 6.0.1  

##3. Instalación

###3.1. Clonar este repo
> cd [USER_HOME]/git 

> git clone https://github.com/cuyum/jbpm-wih.git

###3.2. Construcción
> cd jbpm-wih/wih-rest

> mvn clean install

###3.3. Copiar jar generado a runtime de jbpm6 
> Bajar el servidor BPMS si estuviese levantado 

cp target/**wih-rest.jar** [BMPS_SERVER]/standalone/deployments/business-central.war/WEB-INF/lib

###3.4. Configuración de WIH en BPMS
> Editar archivo **[BPMS_SERVER]/standalone/deployments/business-central.war/WEB-INF/classes/META-INFCustomWorkItemHandlers.conf** y agregar la linea indicada con **"RestJson"** (no olvidar la , de la linea anterior )

```
[
  "Log": new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler(),
  "WebService": new org.jbpm.process.workitem.webservice.WebServiceWorkItemHandler(ksession),
  "Rest": new org.jbpm.process.workitem.rest.RESTWorkItemHandler(),
 "RestJson": new com.cuyum.jbpm.wih.rest.RESTWorkItemHandler()
]
```

###3.5 Agregar propiedades del sistema en BPMS
> Editar el archivo **[BPMS_SERVER]/standalone/configuration/standalone.xml** y agregar las siguientes propiedades

```
<system-properties>
    ...
    <property name="cuyum.jbpm.wih.rest.url.base" value="http://localhost:8080/app"/>
	<property name="cuyum.jbpm.wih.rest.mock.use" value="true"/>
	<property name="cuyum.jbpm.wih.rest.mock.filename" value="/var/wih-mock/mocks.csv"/>
    ...
</system-properties>

```

+ **cuyum.jbpm.wih.rest.url.base** : URL base de aplicación que contiene los servicios REST JSON. EN caso de usar modo MOCK este parametro es ignorado
+ **cuyum.jbpm.wih.rest.mock.use** : "true" en caso de usar el WIH en modo MOCK, este modo no invoca a los srevicios REST reales sino a mocks locales, luego es explica mas en detalle este modo. "false" en caso de invocarse a los servicios REST reales. Default "false"
+ **cuyum.jbpm.wih.rest.mock.filename**: valido en caso de usar modo MOCK, indica el archivo `.csv` de configuración de los mocks. EN caso de usar MOCK este parametro es ignorado.

> Levantar servidor BPMS

##4. Agregar definición de WIH a Workbench
1. Logearse dentro del workbench de BPMS
2. Ir a *Autoria de Proyectos* y elegir el proyecto para el cual quiere agregar el WIH
3. Luego seleccionar *Vista de Repositorio* e ir al directorio **src/main/resources**
4. Ir al menú *Nuevo objeto->Fichero Cargado* y realizar un upload del archivo **[USER_HOME]/git/jbpm-wih/wih-rest/src/main/resources/META-INF/CustomWorkItem.wid**
5. Luego abrir el archivo desde el workbench y validar
6. Ir *Herramientas->Editor de Proyectos* y presionar *Construccion & Implementación*
7. Probar abrir designer de procesos, en la paleta de la izquierda en "Service Tasks" deberia aparecer la actividad **RestJson**


##5. Uso de MOCK
###5.1 Introducción
Se puede simular la llamada a servicios REST directamente desde el WIH y configurar respuestas para determinadas entradas y path de servicios, sin necesidad de desplegar una aplicación adicional que provea los servicios REST. La configuración de estos servicios MOCK se realizan en un archivo CSV (separados por ; ) donde cada linea tiene tres partes:
```
#archivo de prueba para mocks
#[path del servicio rest];[json de entrada];[json de salida esperado]
/path1;{"dato1":"val1","dato2":"val2"};{"resultado":"ok"}
/path1;{"dato1":"val1","dato2":"val3"};{"resultado":"fail","mensaje":"val3 no es correcto"}
/path2;{"dato1":"val1"};{}

```

Dentro de cada JSON de entrada no debe haber espacios, los path deben comenzar con el caracter /
Este archivo se puede modificar y agregar servicios sin necesidad de reinciiar el BPMS.
Esto es solo para pruebas y simulación, no para uso en producción.

###5.2 Configuración
1. Crear un archivo CSV con la estructura mostrada anteriormente y ubicarlo en un directorio teniendo en cuenta que el BPMS tenga al menos acceso de lectura al archivo
2. Configurar el archivo standalone.xml como se indica en la sección 3.5


German Leotta

<gleotta@cuyum.com>

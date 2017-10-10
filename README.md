# JRelEx
Desktop application to explore database data using relations

JRelEx is a java application. It is developed on javafx platform and is intended for searching data using database relations.


### Build

It's a maven project. To succesfully build project you must add activeProfile jrelex to .m2/settings.xml file in a local repository.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" 
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
<activeProfiles>
	<activeProfile>jrelex</activeProfile>
</activeProfiles>
</settings>
```

### Build jar application

```
mvn jfx:jar
```
Jar is stored into jfx/app folder.

### Build native application

```
mvn jfx:native
```
App is stored into jfx/native folder.

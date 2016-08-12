
# gradle

第一步: 添加 JitPack repository
```xml
allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
}
```
第二步: 添加依赖
```xml
dependencies {
        compile 'com.github.izerui:cas-server-integration-redis:1.0.0-RELEASE'
}
```

# maven

第一步: 添加 JitPack repository
```xml
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
</repositories>
```
第二步: 添加依赖
```xml
<dependency>
	    <groupId>com.github.izerui</groupId>
	    <artifactId>cas-server-integration-redis</artifactId>
	    <version>1.0.0-RELEASE</version>
</dependency>
```

[![](https://jitpack.io/v/izerui/cas-server-integration-redis.svg)](https://jitpack.io/#izerui/cas-server-integration-redis)
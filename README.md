
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

cas 4.2.x 替换:

```
<alias name="redisTicketRegistry" alias="ticketRegistry" />
<bean id="redisTicketRegistry" class="ren.boot.cas.ticket.registry.RedisTicketRegistry"
    p:client-ref="ticketRedisTemplate"
    p:tgtTimeout="28800"
    p:stTimeout="10"/>
<bean id="jedisConnFactory"
      class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory"
      p:hostName="192.168.1.236"
      p:database="0"
      p:usePool="true"/>
<bean id="ticketRedisTemplate" class="ren.boot.cas.ticket.registry.TicketRedisTemplate"
      p:connectionFactory-ref="jedisConnFactory"/>

```
[![](https://jitpack.io/v/izerui/cas-server-integration-redis.svg)](https://jitpack.io/#izerui/cas-server-integration-redis)
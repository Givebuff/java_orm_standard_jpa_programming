JPA 시작
==================

## 1. JPA추가
* **Maven** 추가
  * https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa   
  에서 필요한 버전의 jpa dependency 추가
* **Gradle** 추가
  * > implementation 'org.springframework.boot:spring-boot-starter-data-jpa'   
  * 위의 내용을 build.gradle에 추가한다.

## 2. 객체 매핑 어노테이션
``` java
import javax.persistence.*; // jpa 맵핑 어노테이션 패키지

@Entity // 이 클래스를 테이블과 매핑한다고 jpa에게 알려줌. 엔티티 클래스라고 함
@Table(name="MEMBER") // 매핑할 테이블을 지정한다.
public class Member{
  @Id // 기본키 매핑 어노테이션 식별자 필드라 부름
  @Column(name="ID") // 테이블 ID 필드와 id를 매핑
  private String id;
  
  @Column(name="NAME")
  private String username;
  ...
}
```

## 3. 데이터베이스 방언
* 데이터베이스 방언 : mysql, oracle등 다양한 DB에서 문법이 다른걸 뜻함   
* jpa에서 하이버네이트를 사용하는데 여기서 데이터 방언을 지원해줘서 어떤 데이터 베이스를 쓰든 JPA 표준 문법만 사용하면 된다.

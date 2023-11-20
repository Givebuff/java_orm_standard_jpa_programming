고급 매핑
===============
부모 클래스는 추상 클래스로 작성해야한다.
* 상속 관계 매핑 : 객체의 상속 관계를 데이터베이스에 매핑
* @MappedSuperclass : 여러 엔터티에서 공통적으로 사용하는 매핑 정보만 상속
* 복합 키와 식별 관계 매핑 : 데이터베이스의 식별자가 하나 이상일때 매핑하는 방법
* 조인 테이블
* 엔티티 하나에 여러 테이블 매핑

## 1. 상속 관계 매핑
**1-1.  조인 전략**   
* @Inheritance(strategy = InheritanceType.JOINED) : 상속할때 사용해야하는 매핑으로 조인 전략을 사용할때 값을 JOINED로 줘야한다.
* @DiscriminatorColumn(name = "DTYPE") : 부모 클래스의 구분 컬럼 지정. DTYPE이 관례
* @DiscriminatorValue("-") : 엔티티를 저장할때 DTYPE에 -이 저장된다.
* @PrimaryKeyJoinColumn : 기본키를 변경하고 싶을때 사용

```roomsql
CREATE TABLE ITEM(
    ID BIGINT(20) NOT NULL,
    NAME VARCHAR(255),
    PRICE INT(11),
    PRIMARY KEY ("ID")
);

CREATE TABLE ALBUM(
    ID BIGINT(20) NOT NULL
    ARTIST VARCHAR(255)
);

CREATE TABLE BOOK(
    BOOK_ID BIGINT(20) NOT NULL,
    AUTHOR VARCHAR(255),
    ISBN VARCHAR(255),
    PRIMARY KEY ("BOOK_ID")
)
```

```java
import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE")
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;
    
    private String name;
    private int price;
    ...
}

@Entity
@DiscriminatorValue("A")
public class Album extends Item {
    private String artist;
    ...
}

@Entity
@DiscriminatorValue("B")
@PrimaryKeyJoinColumn(name = "BOOK_ID")
public class Book extends Item{
    private String author;
    private String isbn;
    ...
}
```

* 장점
  * 테이블이 정규화 된다.
  * 외래 키 참조 무결성 제약조건을 활용할 수 있다.
  * 저장공간을 효율적으로 사용한다.
* 단점
  * 조회할때 조인을 많이 사용되므로 성능이 저하될 수 있다.
  * 조회 쿼리가 복잡하다.
  * 데이터를 등록할 때 부모, 자식 2번 실행된다.


**1-2. 단일 테이블 전략**
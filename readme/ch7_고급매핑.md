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
하나의 테이블을 사용하여 모든 데이터를 저장하는 전략으로 자식 엔티티가 매핑한 컬럼은 모두 null을 허용해야한다.

```roomsql
CREATE TABLE ITEM (
    ID BIGINT(20) NOT NULL,
    NAME VARCHAR(255),
    PRICE INT,
    ARTIST VARCHAR(255),
    ...,
    DTYPE VARCHAR(255)
)
```
```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE")
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;
    private String name;
    private int price;
}

@Entity
@DiscriminatorValue("A")
public class Album extends Item {
    private String artist;
    ...
}
```
* 장점
  * 조인이 필요없으므로 일반적으로 조회 성능이 빠르다.
  * 조회 쿼리가 단순하다.
* 단점
  * 자식 엔티티가 매핑한 컬럼은 모두 null을 허용해야한다.
  * 테이블이 커져 특정상황에선 조회 속도가 느려질 수 있다.

**1-3. 클래스별 테이블 전략**
이 전략은 자식 엔티티마다 테이블을 만드는데 해당 방법은 추천하지 않는다.
* @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
* 장점
  * 서브 타입을 구분해서 처리할 때 효과적이다.
  * not null 제약조건을 사용할 수 있다.
* 단점
  * 여러 자식 테이블을 함께 조회할 때 성능이 느리다.(SQL에서 UNION을 사용)
  * 자식 테이블을 통합해서 쿼리하기 어렵다.

## 2. MappedSuperclass
* 부모 클래스는 테이블과 매핑하지 않고 부모클래스를 상속받는 자식 클래스에게 매핑 정보만 제공할 때 사용한다.   
* 단순히 매핑 정보를 상속할 목적으로만 사용한다.
* 매핑을 재정의하려면 @AttributeOverride(s)를 사용한다.
* 연관관계를 재정의하려면 @AssociationOverride(s)를 사용한다.
```java
@MappedSuperclass
public abstract class BaseEntity {
    @Id @GeneratedValue
    private Long id;
    private String name;
    @ManyToOne
    private String address;
  ...
}

@Entity
public class Member extends BaseEntity {
    private String email;
  ...
}

@Entity
@AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "SELLER_ID"),
    @AttributeOverride(name = "name", column = @Column(name = "SELLER_NAME")))
})
@AssociationOverride(name = "address", joinColumns = @JoinColumn(name = "ADDR_ID"))
public class Seller extends BaseEntity {
    private String shopName;
  ...
}
```

## 3. 복합 키와 식별 관계 매핑
* 식별 관계 : 부모 테이블의 기본 키를 내려받아 자식테이블의 기본 키 + 외래 키로 사용하는 관계
* 비식별 관계 : 부모 테이블의 기본 키를 받아서 자식 테이블의 외래 키로만 사용하는 관계
  * 필수적 비식별 관계 : 외래 키에 NULL을 허용하지 않고 연관관계를 필수적으로 맺어야 한다.
  * 선택적 비식별 관계 : 외래 키에 NULL을 허용한다. 연관관계를 맺을 지 선택할 수 있다.

**3-1. 복합키**
* @IdClass   
@IdClass는 데이터베이스에 맞춰 사용하는 방법으로 사용할 때 다음 조건을 만족해야 한다.
  * 식별자 클래스의 속성명과 엔티티에서 사용하는 식별자의 속성명이 같아야 한다.
  * Serializable 인터페이스를 구현한다.
  * equals, hashCode를 구현해야한다.
  * 기본 생성자가 있어야한다.
  * 식별자 클래스가 public 이어야한다.

```java
public class ParentId implements Serializable{
    private String id1;
    private String id2;
    
    public ParentId(){}
    
    public ParentId(String id1, String id2){
        this.id1 = id1;
        this.id2 = id2;
    }
    
    @Override
    public boolean equals(Object o){...}

    @Override
    public int hashCode(){...}
}

@Entity
@IdClass(ParentId.class)
public class Parent {
    @Id
    @Column(name = "PARENT_ID1")
    private String id1;
    
    @Id
    @Column(name = "PARENT_ID2")
    private String id2;
    
    private String name;
}

@Entity
public class Child {
    @Id
    private String id;
    
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID1", referencedColumnName = "PARENT_ID1"),
            @JoinColumn(name = "PARENT_ID2", referencedColumnName = "PARENT_ID2")
    })
    private Parent parent;
}

public void test(){
    Parent parent = new Parent();
    parent.setId1("1");
    parent.setId2("2");
    parent.setName("parentName");
    em.persist(parent);
    
    ParentId parentId = new ParentId("1", "2");
    Parent parent1 = em.find(Parent.class, parentId);
}
```

* @EmbeddedId   
@EmbeddedId는 좀 더 객체 지향적으로 매핑하는 방법으로 사용하기 위해선 다음 조건을 만족해야한다.
  * @Embeddable 어노테이션을 붙어주어야한다.
  * Serializable 인터페이스를 구현한다.
  * equals, hashCode를 구현해야한다.
  * 기본 생성자가 있어야한다.
  * 식별자 클래스는 public이어야 한다.

```java

@Embeddable
public class ParentId implements Serializable {
  @Column(name = "PARENT_ID1")
  private String id1;

  @Column(name = "PARENT_ID2")
  private String id2;
  
  public ParentId(){}
  public ParentId(String id1, String id2){
      this.id1 = id1;
      this.id2 = id2;
  }
  
  @Override
  public boolean equals(Object obj) {...}

  @Override
  public int hashCode() {...}
}

@Entity
public class Parent {
    @EmbeddedId
    private ParentId id;
    
    private String name;
  ...
}

void test(){
    Parent parent = new Parent();
    ParentId pId = new ParentId("1", "2");
    // persist
    parent.setId(pId);
    parent.setName("asdf");
    em.persist(parent);
    
    //find
    Parent parent2 = em.find(Parent.class, pId);
}
```

## 4. 조인 테이블
데이터베이스 테이블의 연관관계를 설계하는 방법은 크게 2가지이다.
* 조인 컬럼(MEMBER <--> ROCKER)
* 조인 테이블 사용(MEMBER <--> MEMBER_ROCKER <--> ROCKER)
기본적으로 조인 컬럼을 사용하지만 필요에 따라 조인 테이블을 사용하여야한다.(ex. 다대다 매핑)

**4-1. 일대일 조인 테이블**

```java
@Entity
public class Member {
  ...
  @OneToOne
  @JoinTable(name = "MEMBER_ROCKER",
    joinColumns = @JoinColumn(name = "MEMBER_ID"),
    inverseJoinColumns = @JoinColumn(name = "ROCKER_ID")
  )
  private Rocker rocker;
  ...
}

@Entity
public class Rocker {
  ...
}
```

**4-2. 일대다 조인 테이블**

```java
@Entity
public class Member {
  ...
  @OneToMany
  @JoinTable(name = "MEMBER_ROCKER",
          joinColumns = @JoinColumn(name = "MEMBER_ID"),
          inverseJoinColumns = @JoinColumn(name = "ROCKER_ID")
  )
  private List<Rocker> rockers = new ArrayList<Rocker>();
  ...
}

@Entity
public class Rocker {
  ...
}
```

**4-3. 다대일 조인 테이블**

```java
@Entity
public class Member {
  ...
  @OneToMany(mappedBy = "parent")
  private List<Rocker> rocker = new ArrayList<Rocker>();
  ...
}

@Entity
public class Rocker {
  ...
  @ManyToOne(optional = false)
  @JoinTable(name = "MEMBER_ROCKER",
    joinColumns = @JoinColumn(name = "ROCKER_ID"),
    inverseJoinColumns = @JoinColumn(name = "MEMBER_ID")
  )
  private Member member;
  ...
}
```

**4-4. 다대다 조인 테이블**

```java
@Entity
public class Member {
  ...
  @ManyToMany
  @JoinTable(name = "MEMBER_ROCKER",
          joinColumns = @JoinColumn(name = "ROCKER_ID"),
          inverseJoinColumns = @JoinColumn(name = "MEMBER_ID")
  )
  private List<Rocker> rocker = new ArrayList<Rocker>();
  ...
}

@Entity
public class Rocker {
  ...
  ...
}
```

## 5. 엔티티 하나에 여러 테이블 매핑

```java
@Entity
@Table(name="BOARD")
@SecondaryTable(name="BOARD_DETAIL",
    pkJoinColumns = @PrimaryKeyJoinColumn(name = "BOARD_DETAIL_ID"))
public class Board {
  ...
  @Column(table = "BOARD_DETAIL")
  private String content;
  ...
}
```
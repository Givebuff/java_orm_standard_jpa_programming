엔티티 매핑
=======

## - 대표적 매핑 어노테이션 분류
* 객체와 테이브 매핑 : @Entity, @Table
* 기본키 매핑 : @Id
* 필드와 컬럼 매핑 : @Column
* 연관관계 매핑 : @ManyToOne, @JoinColumn

## 1. @Entity
JPA를 사용해서 테이블과 매핑할 클래스는 @Entity 어노테이션을 필수로 붙여야한다.

|속성|기능|기본값|
|---|---|---|
|name|엔티티 이름 지정. 기본값인 클래스 이름 사용. 다른 패키지의 이름과 겹치지 않게 해야함 | 클래스 이름 그대로 사용|

* 기본 생성자는 필수다
* final, enum, interface, inner 클래스에는 사용할 수 없다.
* 저장할 필드에 final을 사용하면 안된다.

## 2. Table
엔티티와 매핑할 테이블을 지정한다

| 속성                | 기능                                                       | 기본값        |
|-------------------|----------------------------------------------------------|------------|
| name              | 매핑할 테이블 이름                                               | 엔티티 이름을 사용 |
| catalog           | catalog를 매핑한다                                            ||
| schema            | schema를 매핑한다                                             ||
| uniqueConstraints | DDL생성시 유니크 제약조건을 만든다. 스키마 자동 생성 기능을 사용해서 DDL을 만들때만 사용한다. ||

## 3. DB스키마 자동 생성
JPA는 매핑 정보를 기반으로 DB 스키마를 생성한다
persistence.xml에 다음 속성을 추가하면 된다.
```xml
<property name="hibernate.hbm2ddl.auto" value="create"/>
```
그럼 애플리케이션 실행 시점에 테이블을 자동생성한다.

hibernate.hbm2ddl.auto 속성은

| 옵션          | 설명                                                               |
|-------------|------------------------------------------------------------------|
| create      | 기존 테이블을 삭제하고 새로 생성한다. DROP+CREATE                                |
| create-drop | create+어플리케이션을 종료할때 생성한 DDL을 제거한다.                               |
| update      | 테이블과 엔티티 매핑정보를 비교해 변경사항만 수정                                      |
| validate    | 테이블과 엔티티 매핑정보를 비교해 차이가 있으면 경고를 남기고 어플리케이션이 실행되지 않는다. DDL 수정하지 않음 |
| none        | 자동생성 기능을 사용하지 않으려면 속성을 지우거나 이상한 값을 넣으면 된다. none은 이상한 값이다.        |

validate, none 속성은 스테이징, 운영 서버에서 사용할 수 있지만 나머지는 개발단계에서만 사용해야한다.

## 4. 기본키 매핑
기본 키 생성 전략
* 직접 할당 : 기본 키를 애플리케이션에서 직접 할당한다.
* 자동 생성 : 대리 키 사용 방식
  * IDENTITY: 기본 키 생성을 데이터베이스에 위임한다.
  * SEQUENCE: 데이터베이스 시퀀스를 사용해서 기본키 할당
  * TABLE : 키 생성 테이블을 사용한다.

**4-1. 기본 키 직접할당**   
기본 키를 직접 할당시 @Id로 매핑하면 된다.

@Id 적용 가능 자바 타입
* 자바 기본형
* 자바 Wrapper형
* String
* java.util.Date
* java.sql.Date
* java.math.BigDecimal
* java.math.BigInteger

**4-2. IDENTITY 전략**   
기본 키 생성을 데이터베이스에 위임하는 전략으로 주로 MySQL, PostgreSQL등에서 사용된다.   
식별자가 임의로 생성되는 경우 @GeneratedValue로 식별자 생성 전략을 선택해야 한다.

```java
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}

private void test() {
    Board board = new Board();
    em.persist(board);
    System.out.println("board id : " + board.getId());
}
```
**4-3. SEQUENCE 전략**   
시퀸스는 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트다.
ORACLE, H2등의 데이터베이스에서 사용할 수 있다.
```java
@Entity
@SequenceGenerator(
        name = "시퀀스 생성기명",
        sequenceName = "매핑할 시퀀스 이름",
        initalValue = 1, // 기본값
        allocationSize = 1 // 증가값
)
public class Board{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    genrator = "시퀀스 생성기 명")
    private Long id;
}
```

**4-4 TABLE 전략**   
키 생성 전용 테이블을 하나 만들고 여기에 이름과 값을 사용할 컬럼을 만들어 데이터베이스 시퀀스를 흉내내는 전략이다.   
이 전략은 모든 데이터베이스에 적용할 수 있다.
```roomsql
create table my_sequence{
    sequence_name varchar(255) not null,
    next_val bigint,
    primary key (sequence_name)
}
```

```java
@Entity
@TableGenerator(
        name = "식별자 생성기 명",
        table = "my_sequence",
        pkColumnValue = "BOARD_SEQ",
        allocationSize = 1
) // 테이블 컬럼 매핑에 대한 내용이 없는건 해당 컬럼이 기본값이라 그럼 다음 표 참고

public class Board{
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE,
    generator = "식별자 생성기 명")
    private Long id;
}
```
@TableGenerator 속성

| 속성                | 기능          | 기본값                 |
|-------------------|-------------|---------------------|
| name              | 식별자 생성기 명   | 필수                  |
| table             | 키생성 테이블 명   | hibernate_sequences |
| pkColumnName      | 시퀀스 컬럼 명    | sequence_name       |
| valueColumnName   | 시퀀스 값 컬럼명   | next_val            |
| pkColumnValue     | 키로 사용할 값 이름 | 엔티티 이름              |
| initalValue       | 초기값         | 0                   |
| allocationSize    | 증가 하는 수     | 50                  |
| catalog, schema   |             |                     |
| uniqueConstraints | 유니크 제약 조건   |                     |

**4-5. AUTO 전략**   
사용하는 데이터베이스에 따라 키 생성 전략을 자동으로 하나 선택한다.

## 5. 필드와 컬럼 매핑

**5-1. @Column**

| 속성               | 기능                                                                   | 기본값                  |
|------------------|----------------------------------------------------------------------|----------------------|
| name             | 필드와 매핑할 컬럼 이름                                                        | 객체의 필드 이름            |
| nullable         | null값 허용 여부 설정. false = not null                                     | true                 |
| unique           | 테이블에서 하나의 컬럼에 간단한 제약 조건을 걸때 사용                                       |                      |
| columnDefinition | 데이터베이스 컬럼 정보를 직접 줄 수 있다.                                             | 적절할 컬럼 타입 설정         |
| length           | 문자 길이 제약조건, String 타입에만 사용                                           | 255                  |
| precision, scale | BigDecimal 타입에만 사용 가능. precision : 소수점 포함 전체 자리 수, scale :  소수점 자리 수 | precision=19,scale=2 |

```java
@Column(name="name", nullable=false, unique=true, 
        columnDefinition="varchar(100) default 'EMPTY'",
        length=400, precision=10, scale=2)
private BigDecimal cal;
```

**5-2. @Enumerated**   

|속성| 기능                                                                                  |기본값|
|---|-------------------------------------------------------------------------------------|---|
|value| · EnumType.ORDINAL: enum 순서를 데이터베이스에 저장<br> · EnumType.STRING : enum 이름을 데이터베이스에 저장 | EnumType.ORDINAL|

```java
@Enumerated(EnumType.STRING)
private RoleType roleType;
```

ORDINAL 사용 시 중간에 새로운게 추가되면 기존 값을 변경해야 할 수 있으므로 잘 설계해야한다.

**5-3. @Temporal**

|속성|기능|기본값|
|---|---|---|
|value|TemporalType.DATE: 날짜. 데이터베이스 date타입과 매핑 <br> TemporalType.TIME: 시간. 데이터베이스 time타입과 매핑 <br> TemporalType.TIMESTAMP: 날짜 시간. 데이터베이스 timestamp타입과 매핑| |

**5-4. @Lob**   
별도의 속성은 없다. BLOB, CLOB과 매핑한다.
* CLOB : String, char[], java.sql.CLOB
* BLOB : byte[], java.sql.BLOB

**5-5. @Transient**
필드 매핑하지 않고 데이터베이스에 저장, 조회하지 않는다.   
임시로 어떤 값을 보관하고 싶을 때 사용한다.
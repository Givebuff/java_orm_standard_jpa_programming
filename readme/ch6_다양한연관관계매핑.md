다양한 연관관계 매핑
======

* 다대일 단방향[N:1], 다대일 양방향[N:1] : ch5 리드미 참조
* 일대다[1:N] : 다대일, 일대다는 원래 같은 말이지만 주인을 구별하기 위해 구별한것으로 왼쪽이 주인이다.   
일대다는 단방향만 존재하는데 양방향을 사용하기 위해서는 다대일을 사용하면된다.
* 일대일[1:1] : 테이블 관계에서 다대일, 일대다에선 항상 다쪽이 외래키를 가진다. 일대일은 양쪽다 외래키를 가질 수 있다.
* 다대다[N:N] : 관계형 데이터베이스에서 다대다를 만들 수 없다. 그래서 중간에 연관관계 테이블을 하나 더 만들어 연관관계를 저장한다.
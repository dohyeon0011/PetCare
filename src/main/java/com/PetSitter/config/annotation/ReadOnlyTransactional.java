package com.PetSitter.config.annotation;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 읽기 전용 커스텀 어노테이션
 * @Transactional(readOnly = true, propagation = Propagation.SUPPORTS): readOnly로 읽되, 상위(외부) 트랜잭션이 없는 단독 수행인 경우 트랜잭션 없이 조회해서 동작.
 * -> readOnly = true: 조회되는 Entity를 읽기 전용으로 간주하여 Dirty Checking을 하지 않으니, Snapshot을 저장하지 않아 메모리 절약하여 조회 성능 향상. db flush X
 */
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public @interface ReadOnlyTransactional {
}

package com.assignment.analytics_service.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.util.Objects;
public class ProductViewCount implements Persistable<Integer> {

    @Id
    private Integer id;
    private Long count;

    @Transient
    private boolean isNew;

    @Override
    public Integer getId() {
        return id;
    }

    public ProductViewCount(Integer id, Long count, boolean isNew) {
        this.id = id;
        this.count = count;
        this.isNew = isNew;
    }

    public ProductViewCount() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    @Override
    public boolean isNew() {
        return this.isNew || Objects.isNull(id);
    }
}
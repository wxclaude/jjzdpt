/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.sys.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 菜单管理
 *
 */
public class SysModelEntity implements Serializable {


    private String modelId;

    private String modelName;

    private String url;

    private String parentModel;

    private String Sorted;

    private List<SysModelEntity> subModelList;


    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParentModel() {
        return parentModel;
    }

    public void setParentModel(String parentModel) {
        this.parentModel = parentModel;
    }

    public String getSorted() {
        return Sorted;
    }

    public void setSorted(String sorted) {
        Sorted = sorted;
    }

    public List<SysModelEntity> getSubModelList() {
        return subModelList;
    }

    public void setSubModelList(List<SysModelEntity> subModelList) {
        this.subModelList = subModelList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SysModelEntity that = (SysModelEntity) o;
        return Objects.equals(modelId, that.modelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelId);
    }
}

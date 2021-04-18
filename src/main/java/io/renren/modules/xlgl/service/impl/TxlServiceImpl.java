package io.renren.modules.xlgl.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.common.exception.RRException;
import io.renren.common.utils.MyUtils;
import io.renren.datasource.annotation.DataSource;
import io.renren.modules.xlgl.dao.TdkDao;
import io.renren.modules.xlgl.dao.TsbDao;
import io.renren.modules.xlgl.dao.TxlDao;
import io.renren.modules.xlgl.entity.TdkEntity;
import io.renren.modules.xlgl.entity.TxlEntity;
import io.renren.modules.xlgl.entity.TxlInfoEntity;
import io.renren.modules.xlgl.service.TxlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;


@Service("txlService")
public class TxlServiceImpl extends ServiceImpl<TxlDao, TxlEntity> implements TxlService {

    @Autowired
    private TdkDao tdkDao;

    @Autowired
    private TxlDao txlDao;

    @Autowired
    private TsbDao tsbDao;


    @Override
    @DataSource("slave2")
    public IPage<TxlEntity> queryXlByPage(Map<String, Object> params, List<String> sbIdList) {

        int page = Integer.parseInt(String.valueOf(params.get("page")));
        int limit = Integer.parseInt(String.valueOf(params.get("limit")));

        //这里拼接sbId，在tdk中用in查询
        Page<Map<String,Object>> pageParams = new Page(page,limit);

        StringBuffer sbIdSb = new StringBuffer();
        sbIdList.forEach(e->{
            sbIdSb.append(e).append(",");
        });

        params.put("sbIds", MyUtils.formatSqlIn(sbIdSb.toString()));

        IPage<TxlEntity> txlEntityIPage = baseMapper.queryXlByPage(pageParams, params);
        txlEntityIPage.getRecords().forEach(e -> {
            //获取上联设备，下联设备信息
            e.setSlSbMap(tsbDao.getSbById(e.getSlSbId()));
            e.setXlSbMap(tsbDao.getSbById(e.getXlSbId()));
        });

        return txlEntityIPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DataSource("slave2")
    public void deleteXlBatch(List<TxlEntity> txlEntityList) {
        txlEntityList.forEach(e->{
            baseMapper.deleteById(e.getId());
        });
    }

    @Override
    @DataSource("slave2")
    public List<TxlInfoEntity> queryXlInfo(Map<String, Object> params) {

        String xlbh = MapUtil.getStr(params, "xlbh");
        if (StrUtil.isEmpty(xlbh)) {
            throw new RRException("请选择要查询的线路");
        }

        //递归获取该设备的所有上级设备和线路
        List<TxlInfoEntity> list = new ArrayList<>();
        //getSlDkAndSbRecurs(xlbh,list);

        //因为是倒叙向上查，所以这里将list reverse一下
        //CollectionUtil.reverse(list);

        //获取该线路的上级设备
        params.put("upOrDown", "up");
        TxlInfoEntity slInfoEntity = baseMapper.queryDkAndSbInfoByXlbh(params);
        list.add(slInfoEntity);

        //将该线路加入返回的集合找那个
        TxlInfoEntity currentXlEntity = new TxlInfoEntity();
        currentXlEntity.setType(TxlInfoEntity.TYPE_XL);
        currentXlEntity.setXlbh(MapUtil.getStr(params, "xlbh"));
        list.add(currentXlEntity);

        //获取该线路的下级设备
        params.put("upOrDown", "down");
        TxlInfoEntity xlInfoEntity = baseMapper.queryDkAndSbInfoByXlbh(params);

        list.add(xlInfoEntity);
        return list;
    }

    @Override
    @DataSource("slave2")
    public Map<String, Object> getSbById(String sbid) {
        return baseMapper.getSbById(sbid);
    }

    @Override
    @DataSource("slave2")
    public List<TxlEntity> getListLikeXlbh(String xlbh) {
        return baseMapper.selectList(new QueryWrapper<TxlEntity>().like("xlbh", xlbh).orderByDesc("xlbh"));
    }

    @Override
    @DataSource("slave2")
    public List<TxlEntity> getListByXlbh(String xlbh) {
        return baseMapper.selectList(new QueryWrapper<TxlEntity>().eq("xlbh", xlbh));
    }

    @Override
    @DataSource("slave2")
    public List<TxlEntity> listBySlDkIdOrXlDkId(Integer dkId) {
        return baseMapper.selectList(new QueryWrapper<TxlEntity>().eq("sl_dk_id", dkId).or().eq("xl_dk_id", dkId));
    }

    @Override
    @DataSource("slave2")
    public void saveCC(TxlEntity txlEntity) {
        baseMapper.insert(txlEntity);
    }

    @Override
    @DataSource("slave2")
    public void updateByIdCC(TxlEntity txlEntity) {
        baseMapper.updateById(txlEntity);
    }

    private void getSlDkAndSbRecurs(String xlbh,List<TxlInfoEntity> list) {
        //根据线路号先获取上联的端口和设备
        TxlInfoEntity slDkAndSbEntity = baseMapper.getSlDkAndSbEntity(xlbh);

        if (slDkAndSbEntity == null) {
            return;
        }

        //若存在上联端口和设备，则获取该设备的所有端口
        List<TdkEntity> allDkList = tdkDao.selectList(new QueryWrapper<TdkEntity>().eq("sb_id", slDkAndSbEntity.getSbid()));
        List<Object> idList = new ArrayList<>();
        allDkList.forEach(e -> {
            idList.add(e.getId());
        });

        //查看该端口在xl中有没有作为下联使用的端口，有的话查出改xl，并递归，没有则结束递归
        List<TxlEntity> xlList = txlDao.selectList(new QueryWrapper<TxlEntity>().in("xl_dk_id",idList));

        if (CollectionUtil.isEmpty(xlList)) {
            list.add(slDkAndSbEntity);
            return;
        }

        //这里可能存在一个上联设备有多个线路，但是这些线路上联的设备只能有一个，所以这里去上联xlList的第一条
        TxlEntity txlEntity = xlList.get(0);

        //这里需要通过该端口的上联线路获取该设备的上联端口
        slDkAndSbEntity.setSldk(allDkList.stream().filter(e -> e.getId().equals(txlEntity.getXlDkId())).findFirst().get().getDkh());
        list.add(slDkAndSbEntity);

        TxlInfoEntity slXlInfo = new TxlInfoEntity();
        slXlInfo.setType(TxlInfoEntity.TYPE_XL);
        slXlInfo.setXlbh(txlEntity.getXlbh());
        list.add(slXlInfo);

        getSlDkAndSbRecurs(txlEntity.getXlbh(), list);
    }

}

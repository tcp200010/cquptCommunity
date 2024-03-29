package cn.cquptCommunity.gathering.service;

import java.util.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import cn.cquptCommunity.gathering.dao.GatheringDao;
import cn.cquptCommunity.gathering.pojo.Gathering;

/**
 * 活动模块的业务层
 */
@Service
@Transactional
public class GatheringService {

	@Autowired
	private GatheringDao gatheringDao;
	
	@Autowired
	private IdWorker idWorker;

	@Autowired
	private RedisTemplate redisTemplate;


	/**
	 * 查询全部列表
	 * @return
	 */
	public List<Gathering> findAll() {
		return gatheringDao.findAll();
	}

	
	/**
	 * 条件查询+分页
	 * @param whereMap
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<Gathering> findSearch(Map whereMap, int page, int size) {
		Specification<Gathering> specification = createSpecification(whereMap);
		Pageable pageable =  PageRequest.of(page-1, size); //封装分页参数
		return gatheringDao.findAll(specification, pageable);
	}

	
	/**
	 * 条件查询
	 * @param whereMap
	 * @return
	 */
	public List<Gathering> findSearch(Map whereMap) {
		Specification<Gathering> specification = createSpecification(whereMap);
		return gatheringDao.findAll(specification);
	}

	/**
	 * 根据ID查询实体
	 * @param id
	 * @return
	 */
	@Cacheable(value = "gathering",key = "#id")  //@Cacheable注解表示要使用springboot中的cache缓存：其中value属性表示存入缓存中的名称，key表示存入缓存中的唯一标识
	public Gathering findById(String id) {
		//使用@Cacheable注解后会将该方法的返回值存入cache中
		return gatheringDao.findById(id).get();
	}

	/**
	 * 增加
	 * @param gathering
	 */
	public void add(Gathering gathering) {
		gathering.setId( idWorker.nextId()+"" );
		Object value = redisTemplate.opsForValue().get("index:");//从缓存中获取index的值,index表示下标，代表图片序号
		if(value!=null) {
			int index=(Integer) value +1;//图片序号加1
			String imageURL = "http://yangnanfeng.xyz/images/g" + index + ".jpg";
			if (index >= 30) { //一共30张图片，如果累加大于了30，则重新重第一张开始
				index = 0;
			}
			gathering.setImage(imageURL);
			redisTemplate.opsForValue().set("index:",index);
		}
		if(value==null) { //如果从缓存中没有获取到：表示是第一次
			String imageURL = "http://yangnanfeng.xyz/images/g1.jpg";
			gathering.setImage(imageURL);
			redisTemplate.opsForValue().set("index:",1);
		}
		gatheringDao.save(gathering);
	}

	/**
	 * 修改
	 * @param gathering
	 */
	@CacheEvict(value = "gathering",key = "#gathering.id") //@CacheEvict使用这个注解表示移除cache中的对应元素
	public void update(Gathering gathering) {
		gatheringDao.save(gathering);
	}

	/**
	 * 删除
	 * @param id
	 */
	@CacheEvict(value = "gathering",key = "#id")
	public void deleteById(String id) {
		gatheringDao.deleteById(id);
	}

	/**
	 * 动态条件构建
	 * @param searchMap
	 * @return
	 */
	private Specification<Gathering> createSpecification(Map searchMap) {

		return new Specification<Gathering>() {

			@Override
			public Predicate toPredicate(Root<Gathering> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
                // 编号
                if (searchMap.get("id")!=null && !"".equals(searchMap.get("id"))) {
                	predicateList.add(cb.like(root.get("id").as(String.class), "%"+(String)searchMap.get("id")+"%"));
                }
                // 活动名称
                if (searchMap.get("name")!=null && !"".equals(searchMap.get("name"))) {
                	predicateList.add(cb.like(root.get("name").as(String.class), "%"+(String)searchMap.get("name")+"%"));
                }
                // 大会简介
                if (searchMap.get("summary")!=null && !"".equals(searchMap.get("summary"))) {
                	predicateList.add(cb.like(root.get("summary").as(String.class), "%"+(String)searchMap.get("summary")+"%"));
                }
                // 详细说明
                if (searchMap.get("detail")!=null && !"".equals(searchMap.get("detail"))) {
                	predicateList.add(cb.like(root.get("detail").as(String.class), "%"+(String)searchMap.get("detail")+"%"));
                }
                // 主办方
                if (searchMap.get("sponsor")!=null && !"".equals(searchMap.get("sponsor"))) {
                	predicateList.add(cb.like(root.get("sponsor").as(String.class), "%"+(String)searchMap.get("sponsor")+"%"));
                }
                // 活动图片
                if (searchMap.get("image")!=null && !"".equals(searchMap.get("image"))) {
                	predicateList.add(cb.like(root.get("image").as(String.class), "%"+(String)searchMap.get("image")+"%"));
                }
                // 举办地点
                if (searchMap.get("address")!=null && !"".equals(searchMap.get("address"))) {
                	predicateList.add(cb.like(root.get("address").as(String.class), "%"+(String)searchMap.get("address")+"%"));
                }
                // 是否可见
                if (searchMap.get("state")!=null && !"".equals(searchMap.get("state"))) {
                	predicateList.add(cb.like(root.get("state").as(String.class), "%"+(String)searchMap.get("state")+"%"));
                }
                // 城市
                if (searchMap.get("city")!=null && !"".equals(searchMap.get("city"))) {
                	predicateList.add(cb.like(root.get("city").as(String.class), "%"+(String)searchMap.get("city")+"%"));
                }
				
				return cb.and( predicateList.toArray(new Predicate[predicateList.size()]));

			}
		};

	}

	/**
	 * 根据城市查询活动列表并分页显示
	 */
	public Page<Gathering> findByCity(String city, int page, int size) {
		Pageable pageable =  PageRequest.of(page-1, size);//封装分页参数
		return gatheringDao.findByCity(city,pageable);
	}

	/**
	 * 更新活动参与人数
	 * x用来判断是增加还是减少：用户参加活动，x为1，表示参与人数加一；用户退出活动，x为-1，表示参与人数减一
	 */
	public void updateJoinCount(int x,String gatheringID){
		gatheringDao.updateCount(x,gatheringID);
	}
}

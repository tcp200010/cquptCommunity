package cn.cquptCommunity.spit.controller;

import cn.cquptCommunity.spit.pojo.Spit;
import cn.cquptCommunity.spit.service.SpitService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import util.JwtUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * 吐槽模块的控制器
 */

@RestController
@CrossOrigin
@RequestMapping("/spit")
public class spitController {

    @Autowired
    private SpitService spitService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     *查询所有
     */
    @GetMapping
    public Result findAll(){
        return new Result(true, StatusCode.OK,"查询成功",spitService.findAll());
    }

    /**
     * 根据id查询
     */
    @GetMapping("/{spitId}")
    public Result findById(@PathVariable("spitId") String id){
        return new Result(true, StatusCode.OK,"查询成功",spitService.findById(id));
    }

    /**
     * 添加
     */
    @PostMapping
    public Result save(@RequestBody Spit spit){
        //需要判断用户是否登录
        //上述权限的判断交由拦截器帮我们处理,我们只需要直接拿到处理结果
        String token=(String) request.getAttribute("claims_user");
        if(token==null|| "".equals(token)){ //token为空，表示未登录，没有权限
            return new Result(false,StatusCode.ACCESSERROR,"权限不足");
        }
        //否则token不为空，表示有权限
        spitService.save(spit);//save方法无id代表保存,有id代表修改
        return new Result(true, StatusCode.OK,"保存成功");
    }

    /**
     * 修改
     */
    @PutMapping("/{spitId}")
    public Result update(@PathVariable("spitId") String id,@RequestBody Spit spit){
        //需要判断用户是否登录
        //上述权限的判断交由拦截器帮我们处理,我们只需要直接拿到处理结果
        String token=(String) request.getAttribute("claims_user");
        if(token==null|| "".equals(token)){ //token为空，表示未登录，没有权限
            return new Result(false,StatusCode.ACCESSERROR,"权限不足");
        }
        //否则token不为空，表示有权限
        spit.set_id(id);
        spitService.update(spit);
        return new Result(true, StatusCode.OK,"修改成功");
    }

    /**
     * 删除
     */
    @DeleteMapping("/{spitId}")
    public Result delete(@PathVariable("spitId") String id){
        //需要判断用户是否登录
        //上述权限的判断交由拦截器帮我们处理,我们只需要直接拿到处理结果
        String token=(String) request.getAttribute("claims_user");
        if(token==null|| "".equals(token)){ //token为空，表示未登录，没有权限
            return new Result(false,StatusCode.ACCESSERROR,"权限不足");
        }
        //否则token不为空，表示有权限
        spitService.delete(id);
        return new Result(true, StatusCode.OK,"删除成功");
    }

    /**
     * 根据上级id分页查询
     */
    @GetMapping("/comment/{parentid}/{page}/{size}")
    public Result findByParentid(@PathVariable("parentid") String parentid,@PathVariable("parentid") int page,@PathVariable("parentid") int size){
        Page<Spit> pagedata = spitService.findByParentid(parentid, page, size);//分页查询
        return new Result(true,StatusCode.OK,"查询成功",new PageResult<Spit>(pagedata.getTotalElements(),pagedata.getContent()));
    }

    /**
     * 点赞操作
     */
    @PutMapping("/thumbup/{spitId}")
    public Result thumbup(@PathVariable("spitId") String spitId){
        //需要判断用户是否登录
        //上述权限的判断交由拦截器帮我们处理,我们只需要直接拿到处理结果
        String token=(String) request.getAttribute("claims_user");
        if(token==null|| "".equals(token)){ //token为空，表示未登录，没有权限
            return new Result(false,StatusCode.ACCESSERROR,"权限不足");
        }
        //否则token不为空，表示有权限
        //控制重复点赞：点赞一次后就不能再重复点赞
        //先判断用户是否已经点赞
        Claims claims = jwtUtil.parseJWT(token);
        String userid=claims.getId();
        if(redisTemplate.opsForValue().get("thumbup_"+userid)!=null){ //根据用户id去redis中查找，如果找到，则表示当前用户已经点过赞
            return new Result(false,StatusCode.REPERROR,"不能重复点赞");
        }
        spitService.thumbup(spitId);
        //如果点赞成功，就将该用户标识，存入redis缓存中，防止重复点赞
        redisTemplate.opsForValue().set("thumbup_"+userid,"true");
        return new Result(true,StatusCode.OK,"点赞成功");
    }
}

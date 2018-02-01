package com.tale.service;

import java.util.List;
import java.util.Optional;

import com.blade.ioc.annotation.Bean;
import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import com.blade.mvc.annotation.JSON;
import com.blade.mvc.annotation.PostRoute;
import com.tale.model.entity.Comments;
import com.tale.model.entity.Contents;
import com.tale.model.entity.Relationships;
import com.tale.model.entity.Talks;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Bean
public class TalkService {
    
    public List<Talks> getTalks() {
        return new Talks().findAll();
    }
    /**
     * 发布说说
     *
     * @param contents 文章对象
     */
    @PostRoute(value = "publish")
    @JSON
    public Integer publish(Talks talk) {

        //获取当前时间
        int time = DateKit.nowUnix();
        talk.setCreated(time);
        log.info(talk+"");
        Integer cid = talk.save();
        
        return cid;
    }
    /**
     * 根据id获取说说
     *
     * @param id 唯一标识
     */
    public Optional<Talks> getTalks(String id) {
        if (StringKit.isNotBlank(id)) {
            if (StringKit.isNumber(id)) {
                return Optional.ofNullable(new Talks().find(id));
            } else {
                return Optional.ofNullable(new Talks().where("tid", id).find());
            }
        }
        return Optional.empty();
    }
    /**
     * 根据说说id删除
     *
     * @param cid 文章id
     */
    public void delete(int tid) {
        Optional<Talks> talk = this.getTalks(tid + "");
        talk.ifPresent(content -> {
            new Talks().delete(tid);
        });
    }
    public void updateArticle(Talks talk) {
        talk.update(talk.getTid());
        
    }
}

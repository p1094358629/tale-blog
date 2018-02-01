package com.tale.controller.admin;

import java.util.List;
import java.util.Optional;

import com.blade.ioc.annotation.Inject;
import com.blade.jdbc.page.Page;
import com.blade.kit.StringKit;
import com.blade.mvc.annotation.GetRoute;
import com.blade.mvc.annotation.JSON;
import com.blade.mvc.annotation.Param;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.annotation.PathParam;
import com.blade.mvc.annotation.PostRoute;
import com.blade.mvc.annotation.Route;
import com.blade.mvc.http.Request;
import com.blade.mvc.ui.RestResponse;
import com.blade.validator.annotation.Valid;
import com.tale.controller.BaseController;
import com.tale.exception.TipException;
import com.tale.extension.Commons;
import com.tale.model.dto.LogActions;
import com.tale.model.dto.Types;
import com.tale.model.entity.Contents;
import com.tale.model.entity.Logs;
import com.tale.model.entity.Metas;
import com.tale.model.entity.Talks;
import com.tale.model.entity.Users;
import com.tale.service.SiteService;
import com.tale.service.TalkService;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Path("admin/talk")
public class TalkController extends BaseController {
    @Inject
    private TalkService talkService; 
    @Inject
    private SiteService siteService;
    /**
     * 说说管理首页
     *
     * @param page
     * @param limit
     * @param request
     * @return
     */
    @GetRoute(value = "")
    public String index(@Param(defaultValue = "1") int page, @Param(defaultValue = "15") int limit,
                        Request request) {
        Page<Talks> talks = new Talks().page(page, limit, "created desc");
        request.attribute("talks", talks);
        System.err.println(talks);
        return "admin/talk_list";
    }
    /**
     * 说说发布页面
     *
     * @param request
     * @return
     */
    @GetRoute(value = "publishTalks")
    public String newTalk(Request request) {
        //得到所有的标签集合
//        List<Metas> categories = metasService.getMetas(Types.CATEGORY);
//        request.attribute("categories", categories);
//        request.attribute(Types.ATTACH_URL, Commons.site_option(Types.ATTACH_URL, Commons.site_url()));
        return "admin/talk_edit";
    }
    /**
     * 发布说说操作
     *
     * @return
     */
    //FIXME
    @PostRoute(value = "publish")
    @JSON
    public RestResponse publishTalk(@Valid Talks talk) {

        try {
            Integer cid = talkService.publish(talk);
            siteService.cleanCache(Types.C_STATISTICS);
            return RestResponse.ok(cid);
        } catch (Exception e) {
            String msg = "说说发布失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                log.error(msg, e);
            }
            return RestResponse.fail(msg);
        }
    }
    /**
     * 说说编辑页面
     *
     * @param cid
     * @param request
     * @return
     */
    @GetRoute(value = "/:cid")
    public String editArticle(@PathParam String cid, Request request) {
        Optional<Talks> talk = talkService.getTalks(cid);
        if (!talk.isPresent()) {
            return render_404();
        }
        request.attribute("talk", talk.get());
        request.attribute("active", "talk");
        return "admin/talk_edit";
    }
    /**
     * 修改说说操作
     *
     * @return
     */
    @PostRoute(value = "modify")
    @JSON
    public RestResponse modifyArticle(@Valid Talks talk) {
        try {
            if (null == talk || null == talk.getTid()) {
                return RestResponse.fail("缺少参数，请重试");
            }
            Integer cid = talk.getTid();
            talkService.updateArticle(talk);
            return RestResponse.ok(cid);
        } catch (Exception e) {
            String msg = "说说编辑失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                log.error(msg, e);
            }
            return RestResponse.fail(msg);
        }
    }
    /**
     * 删除说说操作
     *
     * @param cid
     * @param request
     * @return
     */
    @Route(value = "delete")
    @JSON
    public RestResponse delete(@Param int tid, Request request) {
        try {
            talkService.delete(tid);
            siteService.cleanCache(Types.C_STATISTICS);
            new Logs(LogActions.DEL_ARTICLE, tid + "", request.address(), this.getUid()).save();
        } catch (Exception e) {
            String msg = "文章删除失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                log.error(msg, e);
            }
            return RestResponse.fail(msg);
        }
        return RestResponse.ok();
    }
}

package com.yingda.lkj.beans.system;

import lombok.Data;

/**
 * <p>分页简化</p>
 * <br/>
 * <p>1.后端对照UserController.userList 前端对照user.html</p>
 * <br/>
 * <p>2.modelAndView中要回传page并setPageTotal</p>
 * <br/>
 * <p>3.每页查询数量pageSize 需要在路径上给出(如：/backstage/user?pageSize=20) </p>
 * <br/>
 * <p>4.前端要使用layui-row</p>
 * <br/>
 * <p>5.layui-form 中要写id， 再加上两个input: </p>
 * <p>      input type="hidden" name="pageSize" th:value="${page.pageSize}</p>
 * <p>      input type="hidden" name="currentPage"</p>
 * <br/>
 * <p>6.在合适的位置 加上：div th:include="/common/page::page('{input的id}')"</p>
 *
 * @author hood  2019/12/27
 */
@Data
public class Page {
    private Long pageTotal;
    private Integer pageSize;
    private Integer currentPage;

    public Page() {
    }

    public Page(Integer pageSize, Integer currentPage) {
        this.pageSize = pageSize;
        this.currentPage = currentPage;
    }
}

// 点击局名时，加载线路表信息
function initLines(table, nodeId) {
    $('#parentTable').next().hide();
    $('#childrenTable').next().hide();
    $('#deviceTable').next().hide();
    table.render({
        elem: '#parentTable',
        url: '/backstage/lineTree/lineInfo',
        height: 800,
        toolbar: "#lineToolbar",
        defaultToolbar: ['filter', 'print', 'exports'],
        cols: [
            [ //表头
                {
                    field: 'name',
                    title: '线路名称',
                    width: 250,
                    sort: true,
                    fixed: 'left',
                    templet: function (d) {
                        return d.name;
                    }
                },
                {field: 'bureauName', title: '局名', width: 250, edit: 'text'},
                {field: 'code', title: '线路编码', width: 148, sort: true},
                {
                    field: 'id',
                    fixed: 'right',
                    title: '操作',
                    width: 415,
                    align: 'center',
                    templet: function (d) {
                        let id = d.id;
                        return "<div class='layui-btn layui-btn layui-btn-xs' data-id='" + id + "' " +
                            "onclick=\"x_admin_show('编辑', '/backstage/line/railwayLineInfo?id='+ this.getAttribute('data-id'), 800, 560)\" >" +
                            "<i class='layui-icon'>&#xe642;</i> 编辑 </div> "
                    }
                }
            ]
        ],
        where: {
            nodeId: nodeId
        },
        page: true,   //开启分页
        limit: 20,   //默认十条数据一页
        limits: [10, 20, 30, 50]  //数据分页条
    });
}

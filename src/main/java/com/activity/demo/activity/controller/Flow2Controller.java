package com.activity.demo.activity.controller;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flow2")
public class Flow2Controller {
    public String currentUser="张三";//模拟当前
    //流程相关
    @Autowired
    private RuntimeService runtimeService;

    //任务相关
    @Autowired
    private TaskService taskService;

    //历史记录相关
    @Autowired
    private HistoryService historyService;

    /**
     * 开启流程,设置其中执行人的方法
     * 1.在流程启动的时候在这里就定义好，
     * @param jobName
     * @return
     */
    @RequestMapping(value = "/start")
    public String start(String jobName) {
        Authentication.setAuthenticatedUserId(jobName);
        Map<String,Object> variable=new HashMap<>();
        variable.put("user1","张三");
        variable.put("user2","李四");
        variable.put("user3","王五");
        String instanceKey = "shenp_1";
        System.out.println("开启流程");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(instanceKey,variable);

        System.out.println("流程实例Id" + processInstance.getId());
        System.out.println("流程定义Id" + processInstance.getProcessDefinitionId());
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
                .processDefinitionId(processInstance.getProcessInstanceId())
                .list();
        System.out.println("根据流程ID查询条数" + list.size());
        return processInstance.getId();
    }

    /**
     * @return
     * @Des:代办查询
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public String query(String userName) {
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("shenp_1")
                .taskAssignee(userName)
                .list();

        if(list!=null && list.size()>0){
            for(Task task:list){
                System.out.println("任务ID:"+task.getId());
                System.out.println("任务名称:"+task.getName());
                System.out.println("任务的创建时间:"+task.getCreateTime());
                System.out.println("任务的办理人:"+task.getAssignee());
                System.out.println("流程实例ID："+task.getProcessInstanceId());
                System.out.println("执行对象ID:"+task.getExecutionId());
                System.out.println("流程定义ID:"+task.getProcessDefinitionId());
                System.out.println("getOwner:"+task.getOwner());
                System.out.println("getCategory:"+task.getCategory());
                System.out.println("getDescription:"+task.getDescription());
                System.out.println("getFormKey:"+task.getFormKey());
                Map<String, Object> map = task.getProcessVariables();
                for (Map.Entry<String, Object> m : map.entrySet()) {
                    System.out.println("key:" + m.getKey() + " value:" + m.getValue());
                }
                for (Map.Entry<String, Object> m : task.getTaskLocalVariables().entrySet()) {
                    System.out.println("key:" + m.getKey() + " value:" + m.getValue());
                }

            }
        }
        return "success";
    }

    /**任务编号从前台传入
     * 完成任务
     */
    @RequestMapping(value = "/finish", method = RequestMethod.GET)
    public String completeTask(String taskId,boolean flag){
        //任务ID
      //  String taskId = "ed8b04a3-7053-11ec-8c5e-e4029b9110bc";

       Map<String,Object> varible=new HashMap<>();
        varible.put("flag",flag);
        taskService.complete(taskId,varible);
        System.out.println("完成任务：任务ID："+taskId);
        return "完成任务：任务ID："+taskId;
    }
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public String queryHistoryTask() {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery() // 创建历史活动实例查询
                .processInstanceId("c3d9feda-70e8-11ec-9410-e4029b9110bc") // 执行流程实例id
                .orderByTaskCreateTime()
                .asc()
                .list();
        for (HistoricTaskInstance hai : list) {
            System.out.println("活动ID:" + hai.getId());
            System.out.println("流程实例ID:" + hai.getProcessInstanceId());
            System.out.println("活动名称：" + hai.getName());
            System.out.println("办理人：" + hai.getAssignee());
            System.out.println("开始时间：" + hai.getStartTime());
            System.out.println("结束时间：" + hai.getEndTime());
        }
        return "ok";
    }

    private String getDiagram(String key){
     return "success";
    }
}

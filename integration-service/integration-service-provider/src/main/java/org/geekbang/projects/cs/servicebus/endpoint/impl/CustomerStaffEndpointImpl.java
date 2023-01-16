package org.geekbang.projects.cs.servicebus.endpoint.impl;

import org.geekbang.projects.cs.integration.domain.PlatformCustomerStaff;
import org.geekbang.projects.cs.integration.domain.OutsourcingSystemDTO;
import org.geekbang.projects.cs.servicebus.endpoint.CustomerStaffEndpoint;
import org.geekbang.projects.cs.servicebus.filter.CustomerStaffFilterChain;
import org.geekbang.projects.cs.servicebus.filter.impl.CustomerStaffEmptyFilter;
import org.geekbang.projects.cs.servicebus.router.OutsourcingSystemRouter;
import org.geekbang.projects.cs.servicebus.router.OutsourcingSystemRouterFactory;
import org.geekbang.projects.cs.servicebus.transformer.CustomerStaffTransformer;
import org.geekbang.projects.cs.servicebus.transformer.CustomerStaffTransformerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerStaffEndpointImpl implements CustomerStaffEndpoint {

    private CustomerStaffFilterChain customerStaffFilterChain;

    public CustomerStaffEndpointImpl() {

        //初始化CustomerStaffFilterChain
        customerStaffFilterChain = new CustomerStaffFilterChain();
        customerStaffFilterChain.addFilter(new CustomerStaffEmptyFilter());
    }

    @Override
    public List<PlatformCustomerStaff> fetchCustomerStaffs(OutsourcingSystemDTO outsourcingSystem) {

        //获取OutsourcingSystemRouter
        OutsourcingSystemRouter outsourcingSystemRouter = OutsourcingSystemRouterFactory.createOutsourcingSystemRouter(outsourcingSystem);

        //通过OutsourcingSystemRouter从远处服务获取原始的异构CustomerStaff列表
        List<Object> originalCustomerStaffs = outsourcingSystemRouter.fetchOutsourcingCustomerStaffs(outsourcingSystem.getSystemUrl());

        //获取CustomerStaffTransformer
        CustomerStaffTransformer customerStaffTransformer = CustomerStaffTransformerFactory.createCustomerStaffTransformer(outsourcingSystem);

        //通过CustomerStaffTransformer将原始数据转换为CustomerStaff
        List<PlatformCustomerStaff> customerStaffs = customerStaffTransformer.transformCustomerStaffs(originalCustomerStaffs);

        //通过CustomerStaffFilterChain对不符合要求的CustomerStaff进行过滤
        List<PlatformCustomerStaff> finalCustomerStaffs = new ArrayList<>();
        customerStaffs.forEach(staff -> {
            PlatformCustomerStaff finalCustomerStaff = customerStaffFilterChain.execute(staff);
            if(finalCustomerStaff != null) {
                finalCustomerStaffs.add(finalCustomerStaff);
            }
        });

        return finalCustomerStaffs;
    }
}

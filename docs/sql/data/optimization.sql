/*接口公开访问*/
UPDATE base_api SET is_open=1 WHERE service_id IN (SELECT route_name FROM gateway_route WHERE route_type='service') AND path NOT LIKE '/actuator%' AND path NOT LIKE '/swagger-resources%' AND path NOT LIKE '%/error';;

/*菜单、操作及权限数据清理*/
DELETE FROM base_menu WHERE parent_id IN (SELECT parent_id FROM (SELECT parent_id FROM base_menu WHERE parent_id!=0 AND parent_id NOT IN (SELECT menu_id FROM base_menu)) tmp);

SELECT * FROM base_role_user WHERE (role_id IS NOT NULL AND role_id NOT IN (SELECT role_id FROM base_role)) OR (user_id IS NOT NULL AND user_id NOT IN (SELECT user_id FROM base_user));
DELETE FROM base_role_user WHERE (role_id IS NOT NULL AND role_id NOT IN (SELECT role_id FROM base_role)) OR (user_id IS NOT NULL AND user_id NOT IN (SELECT user_id FROM base_user));

SELECT * FROM base_menu WHERE menu_id NOT IN (SELECT menu_id FROM base_authority UNION SELECT parent_id AS menu_id FROM base_menu);
DELETE FROM base_menu WHERE menu_id NOT IN (SELECT menu_id FROM (SELECT menu_id FROM base_authority UNION SELECT parent_id AS menu_id FROM base_menu) tmp);

SELECT * FROM base_action WHERE (menu_id IS NOT NULL AND menu_id NOT IN (SELECT menu_id FROM base_menu)) OR (action_id NOT IN (SELECT action_id FROM base_authority));
DELETE FROM base_action WHERE (menu_id IS NOT NULL AND menu_id NOT IN (SELECT menu_id FROM base_menu)) OR (action_id NOT IN (SELECT action_id FROM base_authority));

SELECT * FROM base_authority WHERE (menu_id IS NOT NULL AND menu_id NOT IN (SELECT menu_id FROM base_menu)) OR (action_id IS NOT NULL AND action_id NOT IN (SELECT action_id FROM base_action)) OR (api_id IS NOT NULL AND api_id NOT IN (SELECT api_id FROM base_api));
DELETE FROM base_authority WHERE (menu_id IS NOT NULL AND menu_id NOT IN (SELECT menu_id FROM base_menu)) OR (action_id IS NOT NULL AND action_id NOT IN (SELECT action_id FROM base_action)) OR (api_id IS NOT NULL AND api_id NOT IN (SELECT api_id FROM base_api));

SELECT * FROM base_authority_action WHERE (action_id IS NOT NULL AND action_id NOT IN (SELECT action_id FROM base_action)) OR (authority_id IS NOT NULL AND authority_id NOT IN (SELECT authority_id FROM base_authority));
DELETE FROM base_authority_action WHERE (action_id IS NOT NULL AND action_id NOT IN (SELECT action_id FROM base_action)) OR (authority_id IS NOT NULL AND authority_id NOT IN (SELECT authority_id FROM base_authority));

SELECT * FROM base_authority_app WHERE (app_id IS NOT NULL AND app_id NOT IN (SELECT app_id FROM base_app)) OR (authority_id IS NOT NULL AND authority_id NOT IN (SELECT authority_id FROM base_authority));
DELETE FROM base_authority_app WHERE (app_id IS NOT NULL AND app_id NOT IN (SELECT app_id FROM base_app)) OR (authority_id IS NOT NULL AND authority_id NOT IN (SELECT authority_id FROM base_authority));

SELECT * FROM base_authority_role WHERE (role_id IS NOT NULL AND role_id NOT IN (SELECT role_id FROM base_role)) OR (authority_id IS NOT NULL AND authority_id NOT IN (SELECT authority_id FROM base_authority));
DELETE FROM base_authority_role WHERE (role_id IS NOT NULL AND role_id NOT IN (SELECT role_id FROM base_role)) OR (authority_id IS NOT NULL AND authority_id NOT IN (SELECT authority_id FROM base_authority));

SELECT * FROM base_authority_user WHERE (user_id IS NOT NULL AND user_id NOT IN (SELECT user_id FROM base_user)) OR (authority_id IS NOT NULL AND authority_id NOT IN (SELECT authority_id FROM base_authority));
DELETE FROM base_authority_user WHERE (user_id IS NOT NULL AND user_id NOT IN (SELECT user_id FROM base_user)) OR (authority_id IS NOT NULL AND authority_id NOT IN (SELECT authority_id FROM base_authority));

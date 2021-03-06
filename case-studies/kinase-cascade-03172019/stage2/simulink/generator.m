sys = 'cascade_stage2';
new_system(sys);
open_system(sys);
% Block 'Block __s0 + __s1 <> __s6 (kf = 1.0E-5, kr = 0.1)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block1'],'Position',[70.0, 70.0, 310.0, 490.0]);
set_param([sys '/block1'],'KDfw','99999.99999999999');
set_param([sys '/block1'],'KDrv','10.0');
set_param([sys '/block1'],'kr','1.0');
set_param([sys '/block1'],'kdeg','1.0');
set_param([sys '/block1'],'ratC','0.0');
set_param([sys '/block1'],'n','1.0');
set_param([sys '/block1'],'A_FB_EN','1');
set_param([sys '/block1'],'B_FB_EN','1');
set_param([sys '/block1'],'sel_rate','1');
set_param([sys '/block1'],'sel_Ctot','1');

% Block 'Block __s6 > __s0 + __s7 (kf = 0.1) (for __s0)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block2'],'Position',[450.0, 70.0, 690.0, 490.0]);
set_param([sys '/block2'],'KDfw','10.0');
set_param([sys '/block2'],'KDrv','1.0');
set_param([sys '/block2'],'kr','1.0');
set_param([sys '/block2'],'kdeg','1.0');
set_param([sys '/block2'],'ratC','1.0');
set_param([sys '/block2'],'n','1.0');
set_param([sys '/block2'],'A_FB_EN','1');
set_param([sys '/block2'],'B_FB_EN','0');
set_param([sys '/block2'],'sel_rate','1');
set_param([sys '/block2'],'sel_Ctot','1');

% Block 'Block __s6 > __s0 + __s7 (kf = 0.1) (for __s7)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block3'],'Position',[830.0, 70.0, 1070.0, 490.0]);
set_param([sys '/block3'],'KDfw','10.0');
set_param([sys '/block3'],'KDrv','1.0');
set_param([sys '/block3'],'kr','1.0');
set_param([sys '/block3'],'kdeg','1.0');
set_param([sys '/block3'],'ratC','0.0');
set_param([sys '/block3'],'n','1.0');
set_param([sys '/block3'],'A_FB_EN','0');
set_param([sys '/block3'],'B_FB_EN','0');
set_param([sys '/block3'],'sel_rate','1');
set_param([sys '/block3'],'sel_Ctot','1');

% Block 'Block __s4 + __s7 <> __s8 (kf = 1.0E-5, kr = 0.1)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block4'],'Position',[1210.0, 70.0, 1450.0, 490.0]);
set_param([sys '/block4'],'KDfw','99999.99999999999');
set_param([sys '/block4'],'KDrv','10.0');
set_param([sys '/block4'],'kr','1.0');
set_param([sys '/block4'],'kdeg','1.0');
set_param([sys '/block4'],'ratC','0.0');
set_param([sys '/block4'],'n','1.0');
set_param([sys '/block4'],'A_FB_EN','1');
set_param([sys '/block4'],'B_FB_EN','1');
set_param([sys '/block4'],'sel_rate','1');
set_param([sys '/block4'],'sel_Ctot','1');

% Block 'Block __s2 + __s7 <> __s9 (kf = 1.0E-5, kr = 0.1)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block5'],'Position',[70.0, 630.0, 310.0, 1050.0]);
set_param([sys '/block5'],'KDfw','99999.99999999999');
set_param([sys '/block5'],'KDrv','10.0');
set_param([sys '/block5'],'kr','1.0');
set_param([sys '/block5'],'kdeg','1.0');
set_param([sys '/block5'],'ratC','0.0');
set_param([sys '/block5'],'n','1.0');
set_param([sys '/block5'],'A_FB_EN','1');
set_param([sys '/block5'],'B_FB_EN','0');
set_param([sys '/block5'],'sel_rate','1');
set_param([sys '/block5'],'sel_Ctot','1');


% ** Connections **

% Incoming connections for block '__s0 + __s1 <> __s6 (kf = 1.0E-5, kr = 0.1)'
add_block('simulink/Math Operations/Sum', [sys '/block1_Atot_adder'],'Position',[20.0, 70.0, 40.0, 90.0]);
set_param([sys '/block1_Atot_adder'],'Inputs','++');
add_line(sys, 'block1_Atot_adder/1','block1/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block1_Atot'],'Position',[0.0, 70.0, 20.0, 90.0]);
set_param([sys '/block1_Atot'],'Value','60000.0');
add_line(sys, 'block2/11','block1_Atot_adder/1','autorouting','on');
add_line(sys, 'block1_Atot/1','block1_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block1_Btot'],'Position',[20.0, 130.0, 40.0, 150.0]);
set_param([sys '/block1_Btot'],'Value','70000.0');
add_line(sys, 'block1_Btot/1','block1/2','autorouting','on');
add_line(sys, 'block2/1','block1/3','autorouting','on');
add_line(sys, 'block2/16','block1/6','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block1_Dfree'],'Position',[20.0, 250.0, 40.0, 270.0]);
set_param([sys '/block1_Dfree'],'Value','1.0');
add_line(sys, 'block1_Dfree/1','block1/4','autorouting','on');

% Incoming connections for block '__s6 > __s0 + __s7 (kf = 0.1) (for __s0)'
add_block('simulink/Math Operations/Sum', [sys '/block2_Atot_adder'],'Position',[400.0, 70.0, 420.0, 90.0]);
set_param([sys '/block2_Atot_adder'],'Inputs','++');
add_line(sys, 'block2_Atot_adder/1','block2/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block2_Atot'],'Position',[380.0, 70.0, 400.0, 90.0]);
set_param([sys '/block2_Atot'],'Value','0.0');
add_line(sys, 'block1/11','block2_Atot_adder/1','autorouting','on');
add_line(sys, 'block2_Atot/1','block2_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block2_Btot'],'Position',[400.0, 130.0, 420.0, 150.0]);
set_param([sys '/block2_Btot'],'Value','1.0');
add_line(sys, 'block2_Btot/1','block2/2','autorouting','on');

% Incoming connections for block '__s6 > __s0 + __s7 (kf = 0.1) (for __s7)'
add_block('simulink/Sources/Constant', [sys '/block3_Btot'],'Position',[780.0, 130.0, 800.0, 150.0]);
set_param([sys '/block3_Btot'],'Value','1.0');
add_line(sys, 'block3_Btot/1','block3/2','autorouting','on');
add_line(sys, 'block2/5','block3/6','autorouting','on');
add_line(sys, 'block2/14','block3/5','autorouting','on');

% Incoming connections for block '__s4 + __s7 <> __s8 (kf = 1.0E-5, kr = 0.1)'
add_block('simulink/Sources/Constant', [sys '/block4_Atot'],'Position',[1160.0, 70.0, 1180.0, 90.0]);
set_param([sys '/block4_Atot'],'Value','200000.0');
add_line(sys, 'block4_Atot/1','block4/1','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block4_Btot_adder'],'Position',[1160.0, 130.0, 1180.0, 150.0]);
set_param([sys '/block4_Btot_adder'],'Inputs','+++');
add_line(sys, 'block4_Btot_adder/1','block4/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block4_Btot'],'Position',[1140.0, 130.0, 1160.0, 150.0]);
set_param([sys '/block4_Btot'],'Value','0.0');
add_line(sys, 'block5/9','block4_Btot_adder/1','autorouting','on');
add_line(sys, 'block3/11','block4_Btot_adder/2','autorouting','on');
add_line(sys, 'block4_Btot/1','block4_Btot_adder/3','autorouting','on');
add_line(sys, 'block4/11','block4/3','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block4_Dfree'],'Position',[1160.0, 250.0, 1180.0, 270.0]);
set_param([sys '/block4_Dfree'],'Value','1.0');
add_line(sys, 'block4_Dfree/1','block4/4','autorouting','on');

% Incoming connections for block '__s2 + __s7 <> __s9 (kf = 1.0E-5, kr = 0.1)'
add_block('simulink/Sources/Constant', [sys '/block5_Atot'],'Position',[20.0, 630.0, 40.0, 650.0]);
set_param([sys '/block5_Atot'],'Value','3000000.0');
add_line(sys, 'block5_Atot/1','block5/1','autorouting','on');
add_line(sys, 'block4/3','block5/2','autorouting','on');
add_line(sys, 'block5/11','block5/3','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block5_Dfree'],'Position',[20.0, 810.0, 40.0, 830.0]);
set_param([sys '/block5_Dfree'],'Value','1.0');
add_line(sys, 'block5_Dfree/1','block5/4','autorouting','on');

%Output

add_block('simulink/Commonly Used Blocks/Scope', [sys '/outputscope'],'Position',[1570.0, 50, 1600.0, 80]);
set_param([sys '/outputscope'],'NumInputPorts','8');

% Output block '__s0 + __s1 <> __s6 (kf = 1.0E-5, kr = 0.1)'
add_line(sys, 'block1/1','outputscope/1','autorouting','on');
add_line(sys, 'block1/3','outputscope/2','autorouting','on');

% Output block '__s6 > __s0 + __s7 (kf = 0.1) (for __s0)'
add_line(sys, 'block2/1','outputscope/3','autorouting','on');

% Output block '__s6 > __s0 + __s7 (kf = 0.1) (for __s7)'

% Output block '__s4 + __s7 <> __s8 (kf = 1.0E-5, kr = 0.1)'
add_line(sys, 'block4/1','outputscope/4','autorouting','on');
add_line(sys, 'block4/3','outputscope/5','autorouting','on');
add_line(sys, 'block4/11','outputscope/6','autorouting','on');

% Output block '__s2 + __s7 <> __s9 (kf = 1.0E-5, kr = 0.1)'
add_line(sys, 'block5/1','outputscope/7','autorouting','on');
add_line(sys, 'block5/11','outputscope/8','autorouting','on');

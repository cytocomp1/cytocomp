sys = 'repressor';
new_system(sys);
open_system(sys);
% Block 'Block P__genid_0 + PZ <> B__genid_0 (kf = 7.90569415042095, kr = 1000.0)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block1'],'Position',[70.0, 70.0, 310.0, 490.0]);
set_param([sys '/block1'],'KDfw','3.999999999999999');
set_param([sys '/block1'],'KDrv','10.0');
set_param([sys '/block1'],'kr','1000.0');
set_param([sys '/block1'],'kdeg','1.0');
set_param([sys '/block1'],'ratC','0.0');
set_param([sys '/block1'],'n','2.0');
set_param([sys '/block1'],'A_FB_EN','1');
set_param([sys '/block1'],'B_FB_EN','0');
set_param([sys '/block1'],'sel_rate','1');
set_param([sys '/block1'],'sel_Ctot','1');

% Block 'Block P__genid_0 > X (kf = 0.11560693641618497)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block2'],'Position',[450.0, 70.0, 690.0, 490.0]);
set_param([sys '/block2'],'KDfw','10.0');
set_param([sys '/block2'],'KDrv','10.0');
set_param([sys '/block2'],'kr','0.11560693641618495');
set_param([sys '/block2'],'kdeg','1.0');
set_param([sys '/block2'],'ratC','10.0');
set_param([sys '/block2'],'n','1.0');
set_param([sys '/block2'],'A_FB_EN','0');
set_param([sys '/block2'],'B_FB_EN','0');
set_param([sys '/block2'],'sel_rate','1');
set_param([sys '/block2'],'sel_Ctot','1');

% Block 'Block X > PX (kf = 20.0)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block3'],'Position',[70.0, 630.0, 310.0, 1050.0]);
set_param([sys '/block3'],'KDfw','10.0');
set_param([sys '/block3'],'KDrv','10.0');
set_param([sys '/block3'],'kr','20.0');
set_param([sys '/block3'],'kdeg','1.0');
set_param([sys '/block3'],'ratC','10.0');
set_param([sys '/block3'],'n','1.0');
set_param([sys '/block3'],'A_FB_EN','0');
set_param([sys '/block3'],'B_FB_EN','0');
set_param([sys '/block3'],'sel_rate','1');
set_param([sys '/block3'],'sel_Ctot','1');

% Block 'Block P__genid_0 + PX <> B__genid_0 (kf = 7.90569415042095, kr = 1000.0)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block4'],'Position',[450.0, 630.0, 690.0, 1050.0]);
set_param([sys '/block4'],'KDfw','3.999999999999999');
set_param([sys '/block4'],'KDrv','10.0');
set_param([sys '/block4'],'kr','1000.0');
set_param([sys '/block4'],'kdeg','1.0');
set_param([sys '/block4'],'ratC','0.0');
set_param([sys '/block4'],'n','2.0');
set_param([sys '/block4'],'A_FB_EN','1');
set_param([sys '/block4'],'B_FB_EN','0');
set_param([sys '/block4'],'sel_rate','1');
set_param([sys '/block4'],'sel_Ctot','1');

% Block 'Block P__genid_0 > Y (kf = 0.11560693641618497)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block5'],'Position',[70.0, 1190.0, 310.0, 1610.0]);
set_param([sys '/block5'],'KDfw','10.0');
set_param([sys '/block5'],'KDrv','10.0');
set_param([sys '/block5'],'kr','0.11560693641618495');
set_param([sys '/block5'],'kdeg','1.0');
set_param([sys '/block5'],'ratC','0.0');
set_param([sys '/block5'],'n','1.0');
set_param([sys '/block5'],'A_FB_EN','0');
set_param([sys '/block5'],'B_FB_EN','0');
set_param([sys '/block5'],'sel_rate','1');
set_param([sys '/block5'],'sel_Ctot','1');

% Block 'Block Y > PY (kf = 20.0)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block6'],'Position',[450.0, 1190.0, 690.0, 1610.0]);
set_param([sys '/block6'],'KDfw','10.0');
set_param([sys '/block6'],'KDrv','10.0');
set_param([sys '/block6'],'kr','20.0');
set_param([sys '/block6'],'kdeg','1.0');
set_param([sys '/block6'],'ratC','0.0');
set_param([sys '/block6'],'n','1.0');
set_param([sys '/block6'],'A_FB_EN','0');
set_param([sys '/block6'],'B_FB_EN','0');
set_param([sys '/block6'],'sel_rate','1');
set_param([sys '/block6'],'sel_Ctot','1');

% Block 'Block P__genid_0 + PY <> B__genid_0 (kf = 7.90569415042095, kr = 1000.0)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block7'],'Position',[70.0, 1750.0, 310.0, 2170.0]);
set_param([sys '/block7'],'KDfw','3.999999999999999');
set_param([sys '/block7'],'KDrv','10.0');
set_param([sys '/block7'],'kr','1000.0');
set_param([sys '/block7'],'kdeg','1.0');
set_param([sys '/block7'],'ratC','0.0');
set_param([sys '/block7'],'n','2.0');
set_param([sys '/block7'],'A_FB_EN','1');
set_param([sys '/block7'],'B_FB_EN','0');
set_param([sys '/block7'],'sel_rate','1');
set_param([sys '/block7'],'sel_Ctot','1');

% Block 'Block P__genid_0 > Z (kf = 0.11560693641618497)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block8'],'Position',[450.0, 1750.0, 690.0, 2170.0]);
set_param([sys '/block8'],'KDfw','10.0');
set_param([sys '/block8'],'KDrv','10.0');
set_param([sys '/block8'],'kr','0.11560693641618495');
set_param([sys '/block8'],'kdeg','1.0');
set_param([sys '/block8'],'ratC','10.0');
set_param([sys '/block8'],'n','1.0');
set_param([sys '/block8'],'A_FB_EN','0');
set_param([sys '/block8'],'B_FB_EN','0');
set_param([sys '/block8'],'sel_rate','1');
set_param([sys '/block8'],'sel_Ctot','1');

% Block 'Block Z > PZ (kf = 20.0)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block9'],'Position',[70.0, 2310.0, 310.0, 2730.0]);
set_param([sys '/block9'],'KDfw','10.0');
set_param([sys '/block9'],'KDrv','10.0');
set_param([sys '/block9'],'kr','20.0');
set_param([sys '/block9'],'kdeg','1.0');
set_param([sys '/block9'],'ratC','10.0');
set_param([sys '/block9'],'n','1.0');
set_param([sys '/block9'],'A_FB_EN','0');
set_param([sys '/block9'],'B_FB_EN','0');
set_param([sys '/block9'],'sel_rate','1');
set_param([sys '/block9'],'sel_Ctot','1');


% ** Connections **

% Incoming connections for block 'P__genid_0 + PZ <> B__genid_0 (kf = 7.90569415042095, kr = 1000.0)'
add_block('simulink/Math Operations/Sum', [sys '/block1_Btot_adder'],'Position',[20.0, 130.0, 40.0, 150.0]);
set_param([sys '/block1_Btot_adder'],'Inputs','++');
add_line(sys, 'block1_Btot_adder/1','block1/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block1_Btot'],'Position',[0.0, 130.0, 20.0, 150.0]);
set_param([sys '/block1_Btot'],'Value','0.0');
add_line(sys, 'block9/11','block1_Btot_adder/1','autorouting','on');
add_line(sys, 'block1_Btot/1','block1_Btot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block1_Dfree'],'Position',[20.0, 250.0, 40.0, 270.0]);
set_param([sys '/block1_Dfree'],'Value','10.0');
add_line(sys, 'block1_Dfree/1','block1/4','autorouting','on');

% Incoming connections for block 'P__genid_0 > X (kf = 0.11560693641618497)'
add_block('simulink/Sources/Constant', [sys '/block2_Btot'],'Position',[400.0, 130.0, 420.0, 150.0]);
set_param([sys '/block2_Btot'],'Value','10.0');
add_line(sys, 'block2_Btot/1','block2/2','autorouting','on');

% Incoming connections for block 'X > PX (kf = 20.0)'
add_block('simulink/Math Operations/Sum', [sys '/block3_Atot_adder'],'Position',[20.0, 630.0, 40.0, 650.0]);
set_param([sys '/block3_Atot_adder'],'Inputs','++');
add_line(sys, 'block3_Atot_adder/1','block3/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block3_Atot'],'Position',[0.0, 630.0, 20.0, 650.0]);
set_param([sys '/block3_Atot'],'Value','0.0');
add_line(sys, 'block2/11','block3_Atot_adder/1','autorouting','on');
add_line(sys, 'block3_Atot/1','block3_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block3_Btot'],'Position',[20.0, 690.0, 40.0, 710.0]);
set_param([sys '/block3_Btot'],'Value','10.0');
add_line(sys, 'block3_Btot/1','block3/2','autorouting','on');

% Incoming connections for block 'P__genid_0 + PX <> B__genid_0 (kf = 7.90569415042095, kr = 1000.0)'
add_block('simulink/Math Operations/Sum', [sys '/block4_Btot_adder'],'Position',[400.0, 690.0, 420.0, 710.0]);
set_param([sys '/block4_Btot_adder'],'Inputs','++');
add_line(sys, 'block4_Btot_adder/1','block4/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block4_Btot'],'Position',[380.0, 690.0, 400.0, 710.0]);
set_param([sys '/block4_Btot'],'Value','0.0');
add_line(sys, 'block3/11','block4_Btot_adder/1','autorouting','on');
add_line(sys, 'block4_Btot/1','block4_Btot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block4_Dfree'],'Position',[400.0, 810.0, 420.0, 830.0]);
set_param([sys '/block4_Dfree'],'Value','10.0');
add_line(sys, 'block4_Dfree/1','block4/4','autorouting','on');

% Incoming connections for block 'P__genid_0 > Y (kf = 0.11560693641618497)'
add_block('simulink/Sources/Constant', [sys '/block5_Btot'],'Position',[20.0, 1250.0, 40.0, 1270.0]);
set_param([sys '/block5_Btot'],'Value','10.0');
add_line(sys, 'block5_Btot/1','block5/2','autorouting','on');

% Incoming connections for block 'Y > PY (kf = 20.0)'
add_block('simulink/Math Operations/Sum', [sys '/block6_Atot_adder'],'Position',[400.0, 1190.0, 420.0, 1210.0]);
set_param([sys '/block6_Atot_adder'],'Inputs','++');
add_line(sys, 'block6_Atot_adder/1','block6/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block6_Atot'],'Position',[380.0, 1190.0, 400.0, 1210.0]);
set_param([sys '/block6_Atot'],'Value','100.0');
add_line(sys, 'block5/11','block6_Atot_adder/1','autorouting','on');
add_line(sys, 'block6_Atot/1','block6_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block6_Btot'],'Position',[400.0, 1250.0, 420.0, 1270.0]);
set_param([sys '/block6_Btot'],'Value','10.0');
add_line(sys, 'block6_Btot/1','block6/2','autorouting','on');

% Incoming connections for block 'P__genid_0 + PY <> B__genid_0 (kf = 7.90569415042095, kr = 1000.0)'
add_block('simulink/Sources/Constant', [sys '/block7_Atot'],'Position',[20.0, 1750.0, 40.0, 1770.0]);
set_param([sys '/block7_Atot'],'Value','865.0');
add_line(sys, 'block7_Atot/1','block7/1','autorouting','on');
add_block('simulink/Math Operations/Sum', [sys '/block7_Btot_adder'],'Position',[20.0, 1810.0, 40.0, 1830.0]);
set_param([sys '/block7_Btot_adder'],'Inputs','++');
add_line(sys, 'block7_Btot_adder/1','block7/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block7_Btot'],'Position',[0.0, 1810.0, 20.0, 1830.0]);
set_param([sys '/block7_Btot'],'Value','0.0');
add_line(sys, 'block6/11','block7_Btot_adder/1','autorouting','on');
add_line(sys, 'block7_Btot/1','block7_Btot_adder/2','autorouting','on');
add_line(sys, 'block7/11','block7/3','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block7_Dfree'],'Position',[20.0, 1930.0, 40.0, 1950.0]);
set_param([sys '/block7_Dfree'],'Value','10.0');
add_line(sys, 'block7_Dfree/1','block7/4','autorouting','on');

% Incoming connections for block 'P__genid_0 > Z (kf = 0.11560693641618497)'
add_line(sys, 'block7/1','block8/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block8_Btot'],'Position',[400.0, 1810.0, 420.0, 1830.0]);
set_param([sys '/block8_Btot'],'Value','10.0');
add_line(sys, 'block8_Btot/1','block8/2','autorouting','on');

% Incoming connections for block 'Z > PZ (kf = 20.0)'
add_block('simulink/Math Operations/Sum', [sys '/block9_Atot_adder'],'Position',[20.0, 2310.0, 40.0, 2330.0]);
set_param([sys '/block9_Atot_adder'],'Inputs','++');
add_line(sys, 'block9_Atot_adder/1','block9/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block9_Atot'],'Position',[0.0, 2310.0, 20.0, 2330.0]);
set_param([sys '/block9_Atot'],'Value','0.0');
add_line(sys, 'block8/11','block9_Atot_adder/1','autorouting','on');
add_line(sys, 'block9_Atot/1','block9_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block9_Btot'],'Position',[20.0, 2370.0, 40.0, 2390.0]);
set_param([sys '/block9_Btot'],'Value','10.0');
add_line(sys, 'block9_Btot/1','block9/2','autorouting','on');

%Output

add_block('simulink/Commonly Used Blocks/Scope', [sys '/outputscope'],'Position',[810.0, 50, 840.0, 80]);
set_param([sys '/outputscope'],'NumInputPorts','8');

% Output block 'P__genid_0 + PZ <> B__genid_0 (kf = 7.90569415042095, kr = 1000.0)'
add_line(sys, 'block1/3','outputscope/1','autorouting','on');

% Output block 'P__genid_0 > X (kf = 0.11560693641618497)'

% Output block 'X > PX (kf = 20.0)'
add_line(sys, 'block3/1','outputscope/2','autorouting','on');

% Output block 'P__genid_0 + PX <> B__genid_0 (kf = 7.90569415042095, kr = 1000.0)'
add_line(sys, 'block4/3','outputscope/3','autorouting','on');

% Output block 'P__genid_0 > Y (kf = 0.11560693641618497)'

% Output block 'Y > PY (kf = 20.0)'
add_line(sys, 'block6/1','outputscope/4','autorouting','on');

% Output block 'P__genid_0 + PY <> B__genid_0 (kf = 7.90569415042095, kr = 1000.0)'
add_line(sys, 'block7/1','outputscope/5','autorouting','on');
add_line(sys, 'block7/3','outputscope/6','autorouting','on');
add_line(sys, 'block7/11','outputscope/7','autorouting','on');

% Output block 'P__genid_0 > Z (kf = 0.11560693641618497)'

% Output block 'Z > PZ (kf = 20.0)'
add_line(sys, 'block9/1','outputscope/8','autorouting','on');

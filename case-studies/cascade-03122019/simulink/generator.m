sys = 'cascade';
new_system(sys);
open_system(sys);
% Block 'Block ∅ > A (kf = 100.0)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block1'],'Position',[70.0, 70.0, 310.0, 490.0]);
set_param([sys '/block1'],'KDfw','0.01');
set_param([sys '/block1'],'KDrv','1.0');
set_param([sys '/block1'],'kr','1.0');
set_param([sys '/block1'],'kdeg','1.0');
set_param([sys '/block1'],'ratC','0.0');
set_param([sys '/block1'],'n','1.0');
set_param([sys '/block1'],'A_FB_EN','0');
set_param([sys '/block1'],'B_FB_EN','0');
set_param([sys '/block1'],'sel_rate','1');
set_param([sys '/block1'],'sel_Ctot','1');

% Block 'Block A <> B (kf = 100.0, kr = 50.0)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block2'],'Position',[450.0, 70.0, 690.0, 490.0]);
set_param([sys '/block2'],'KDfw','0.01');
set_param([sys '/block2'],'KDrv','0.02');
set_param([sys '/block2'],'kr','1.0');
set_param([sys '/block2'],'kdeg','1.0');
set_param([sys '/block2'],'ratC','0.0');
set_param([sys '/block2'],'n','1.0');
set_param([sys '/block2'],'A_FB_EN','1');
set_param([sys '/block2'],'B_FB_EN','0');
set_param([sys '/block2'],'sel_rate','1');
set_param([sys '/block2'],'sel_Ctot','1');

% Block 'Block B <> C (kf = 100.0, kr = 50.0)'
add_block('Chip_Library/Aug15_Jon1',[sys '/block3'],'Position',[830.0, 70.0, 1070.0, 490.0]);
set_param([sys '/block3'],'KDfw','0.01');
set_param([sys '/block3'],'KDrv','0.02');
set_param([sys '/block3'],'kr','1.0');
set_param([sys '/block3'],'kdeg','1.0');
set_param([sys '/block3'],'ratC','20.0');
set_param([sys '/block3'],'n','1.0');
set_param([sys '/block3'],'A_FB_EN','1');
set_param([sys '/block3'],'B_FB_EN','0');
set_param([sys '/block3'],'sel_rate','1');
set_param([sys '/block3'],'sel_Ctot','1');


% ** Connections **

% Incoming connections for block '∅ > A (kf = 100.0)'
add_block('simulink/Sources/Constant', [sys '/block1_Atot'],'Position',[20.0, 70.0, 40.0, 90.0]);
set_param([sys '/block1_Atot'],'Value','1.0');
add_line(sys, 'block1_Atot/1','block1/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block1_Btot'],'Position',[20.0, 130.0, 40.0, 150.0]);
set_param([sys '/block1_Btot'],'Value','1.0');
add_line(sys, 'block1_Btot/1','block1/2','autorouting','on');
add_line(sys, 'block2/16','block1/5','autorouting','on');

% Incoming connections for block 'A <> B (kf = 100.0, kr = 50.0)'
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
add_line(sys, 'block3/1','block2/3','autorouting','on');
add_line(sys, 'block3/16','block2/5','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block2_Dfree'],'Position',[400.0, 250.0, 420.0, 270.0]);
set_param([sys '/block2_Dfree'],'Value','1.0');
add_line(sys, 'block2_Dfree/1','block2/4','autorouting','on');

% Incoming connections for block 'B <> C (kf = 100.0, kr = 50.0)'
add_block('simulink/Math Operations/Sum', [sys '/block3_Atot_adder'],'Position',[780.0, 70.0, 800.0, 90.0]);
set_param([sys '/block3_Atot_adder'],'Inputs','++');
add_line(sys, 'block3_Atot_adder/1','block3/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block3_Atot'],'Position',[760.0, 70.0, 780.0, 90.0]);
set_param([sys '/block3_Atot'],'Value','0.0');
add_line(sys, 'block2/11','block3_Atot_adder/1','autorouting','on');
add_line(sys, 'block3_Atot/1','block3_Atot_adder/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block3_Btot'],'Position',[780.0, 130.0, 800.0, 150.0]);
set_param([sys '/block3_Btot'],'Value','1.0');
add_line(sys, 'block3_Btot/1','block3/2','autorouting','on');
add_line(sys, 'block3/11','block3/3','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block3_Dfree'],'Position',[780.0, 250.0, 800.0, 270.0]);
set_param([sys '/block3_Dfree'],'Value','1.0');
add_line(sys, 'block3_Dfree/1','block3/4','autorouting','on');

%Output

add_block('simulink/Commonly Used Blocks/Scope', [sys '/outputscope'],'Position',[1190.0, 50, 1220.0, 80]);
set_param([sys '/outputscope'],'NumInputPorts','3');

% Output block '∅ > A (kf = 100.0)'

% Output block 'A <> B (kf = 100.0, kr = 50.0)'
add_line(sys, 'block2/1','outputscope/1','autorouting','on');

% Output block 'B <> C (kf = 100.0, kr = 50.0)'
add_line(sys, 'block3/1','outputscope/2','autorouting','on');
add_line(sys, 'block3/11','outputscope/3','autorouting','on');

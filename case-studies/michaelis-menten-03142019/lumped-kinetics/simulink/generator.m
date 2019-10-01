sys = 'michaelis_menten';
new_system(sys);
open_system(sys);
% Block 'Block E + S <> ES (kf = 0.001, kr = -9.99)'
add_block('Chip_Library/Aug15_Jon',[sys '/block1'],'Position',[70.0, 70.0, 310.0, 490.0]);
set_param([sys '/block1'],'KDfw','10.0');
set_param([sys '/block1'],'KDrv','10.0');
set_param([sys '/block1'],'kr','0.001');
set_param([sys '/block1'],'kdeg','1.0');
set_param([sys '/block1'],'ratC','0.0');
set_param([sys '/block1'],'n','1.0');
set_param([sys '/block1'],'A_FB_EN','1');
set_param([sys '/block1'],'B_FB_EN','1');
add_block('simulink/Sources/Constant', [sys '/block1_Atot'],'Position',[20.0, 70.0, 40.0, 90.0]);
add_line(sys, 'block1_Atot/1','block1/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block1_Btot'],'Position',[20.0, 140.0, 40.0, 160.0]);
add_line(sys, 'block1_Btot/1','block1/2','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block1_Dfree'],'Position',[20.0, 280.0, 40.0, 300.0]);
add_line(sys, 'block1_Dfree/1','block1/4','autorouting','on');

% Block 'Block ES > P + E (kf = 10.0)'
add_block('Chip_Library/Aug15_Jon',[sys '/block2'],'Position',[450.0, 70.0, 690.0, 490.0]);
set_param([sys '/block2'],'KDfw','10.0');
set_param([sys '/block2'],'KDrv','10.0');
set_param([sys '/block2'],'kr','10.0');
set_param([sys '/block2'],'kdeg','1.0');
set_param([sys '/block2'],'ratC','0.0');
set_param([sys '/block2'],'n','1.0');
set_param([sys '/block2'],'A_FB_EN','1');
set_param([sys '/block2'],'B_FB_EN','0');
add_block('simulink/Sources/Constant', [sys '/block2_Btot'],'Position',[400.0, 140.0, 420.0, 160.0]);
add_line(sys, 'block2_Btot/1','block2/2','autorouting','on');

add_line(sys,'block1/0','block2/11','autorouting','on');
add_line(sys,'block2/11','block1/0','autorouting','on');
add_line(sys,'block1/2','block2/12','autorouting','on');
add_line(sys,'block2/12','block1/2','autorouting','on');
add_line(sys,'block2/0','block1/11','autorouting','on');
add_line(sys,'block1/11','block2/0','autorouting','on');

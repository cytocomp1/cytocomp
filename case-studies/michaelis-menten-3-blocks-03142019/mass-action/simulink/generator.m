sys = 'cascade';
new_system(sys);
open_system(sys);
% Block 'Block X > S (kf = 5.0)'
add_block('Chip_Library/Aug15_Jon',[sys '/block1'],'Position',[70.0, 70.0, 310.0, 490.0]);
set_param([sys '/block1'],'KDfw','10.0');
set_param([sys '/block1'],'KDrv','50.0');
set_param([sys '/block1'],'kr','5.0');
set_param([sys '/block1'],'kdeg','1.0');
set_param([sys '/block1'],'ratC','0.0');
set_param([sys '/block1'],'n','1.0');
set_param([sys '/block1'],'A_FB_EN','1');
set_param([sys '/block1'],'B_FB_EN','0');
add_block('simulink/Sources/Constant', [sys '/block1_Atot'],'Position',[20.0, 70.0, 40.0, 90.0]);
add_line(sys, 'block1_Atot/1','block1/1','autorouting','on');
add_block('simulink/Sources/Constant', [sys '/block1_Btot'],'Position',[20.0, 140.0, 40.0, 160.0]);
add_line(sys, 'block1_Btot/1','block1/2','autorouting','on');

% Block 'Block E + S <> ES (kf = 0.5, kr = 5.0)'
add_block('Chip_Library/Aug15_Jon',[sys '/block2'],'Position',[450.0, 70.0, 690.0, 490.0]);
set_param([sys '/block2'],'KDfw','10.0');
set_param([sys '/block2'],'KDrv','10.0');
set_param([sys '/block2'],'kr','5.0');
set_param([sys '/block2'],'kdeg','1.0');
set_param([sys '/block2'],'ratC','0.0');
set_param([sys '/block2'],'n','1.0');
set_param([sys '/block2'],'A_FB_EN','1');
set_param([sys '/block2'],'B_FB_EN','1');
add_block('simulink/Sources/Constant', [sys '/block2_Dfree'],'Position',[400.0, 280.0, 420.0, 300.0]);
add_line(sys, 'block2_Dfree/1','block2/4','autorouting','on');

% Block 'Block ES > P + E (kf = 5.0)'
add_block('Chip_Library/Aug15_Jon',[sys '/block3'],'Position',[830.0, 70.0, 1070.0, 490.0]);
set_param([sys '/block3'],'KDfw','100.0');
set_param([sys '/block3'],'KDrv','10.0');
set_param([sys '/block3'],'kr','5.0');
set_param([sys '/block3'],'kdeg','1.0');
set_param([sys '/block3'],'ratC','0.0');
set_param([sys '/block3'],'n','1.0');
set_param([sys '/block3'],'A_FB_EN','1');
set_param([sys '/block3'],'B_FB_EN','0');

add_line(sys,'block2/0','block3/11','autorouting','on');
add_line(sys,'block3/11','block2/0','autorouting','on');
add_line(sys,'block2/1','block1/11','autorouting','on');
add_line(sys,'block1/11','block2/1','autorouting','on');
add_line(sys,'block2/2','block3/12','autorouting','on');
add_line(sys,'block3/12','block2/2','autorouting','on');
add_line(sys,'block3/0','block2/11','autorouting','on');
add_line(sys,'block2/11','block3/0','autorouting','on');

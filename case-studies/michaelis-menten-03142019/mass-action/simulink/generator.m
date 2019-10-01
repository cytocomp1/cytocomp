sys = 'cascade';
new_system(sys);
open_system(sys);
% Block 'Block E + S > ES '
add_block('Chip_Library/Aug15_Jon',[sys '/block1'],'Position',[70.0, 70.0, 190.0, 490.0]);
set_param([sys '/block1'],'KDfw','10.0');
set_param([sys '/block1'],'KDrv','10.0');
set_param([sys '/block1'],'kr','10.0');
set_param([sys '/block1'],'kdeg','1.0');
set_param([sys '/block1'],'ratC','0.0');
set_param([sys '/block1'],'n','1.0');
set_param([sys '/block1'],'A_FB_EN','1');
set_param([sys '/block1'],'B_FB_EN','1');

% Block 'Block ES > P '
add_block('Chip_Library/Aug15_Jon',[sys '/block2'],'Position',[330.0, 70.0, 450.0, 490.0]);
set_param([sys '/block2'],'KDfw','10.0');
set_param([sys '/block2'],'KDrv','10.0');
set_param([sys '/block2'],'kr','1.0');
set_param([sys '/block2'],'kdeg','1.0');
set_param([sys '/block2'],'ratC','0.0');
set_param([sys '/block2'],'n','1.0');
set_param([sys '/block2'],'A_FB_EN','1');
set_param([sys '/block2'],'B_FB_EN','0');
set_param([sys '/block2'],'Btot','10.0');

add_line(sys,'block1/2','block2/12','autorouting','on');
add_line(sys,'block2/12','block1/2','autorouting','on');
add_line(sys,'block2/0','block1/9','autorouting','on');
add_line(sys,'block1/9','block2/0','autorouting','on');
add_line(sys,'block2/2','block2/9','autorouting','on');
add_line(sys,'block2/9','block2/2','autorouting','on');

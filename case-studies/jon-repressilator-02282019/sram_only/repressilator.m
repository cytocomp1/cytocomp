function prog_vect=Jon_Bigboard_Repressilator(prog_vect,FPGA_lib,FPGA_xptr)

% 150p, 100n, 150p, 400n, 150p, 1u, 150p
% IStotSweep(1,:) = digital_current_to_closest_bits('B00',100,1);
%
% numPoints=length(IStotSweep(:,1));
% initSamples = 23;
% numSamples=500;
% Imeas=zeros(initSamples+numPoints*numSamples,7); %%%%%%%%%%%%%%%%%%%%%%% note!

%% ADC
% Scale factor from ADC output to analog current (when clk=5MHz)
ADC_scale=calibrated('ADC','Aug15',28); %5Mhz

% program ADC scale factors in the FPGA
scalefactors = [40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40];
chip_addr = 0;
DNAChip_program_chip_FPGA_initparam_aug15(FPGA_lib,FPGA_xptr,scalefactors,chip_addr);

% figure(1); clf; figure(2); clf;
% options={'o-','x-','+-','*-'};

%% set SRAM connection
%DNAChip_reset_sram_aug15_bigboard_repressilator_prtnchip(FPGA_lib,FPGA_xptr);
tstart=tic; % setting the start point for elapsed time measurement

in_all=repressilator_sram();

disp('Programming connection SRAM...')
pause(1);

%program_FPGA_sram_bigboard(FPGA_lib,FPGA_xptr,sram_data_prtn,sram_data_gene)
sram_data_prtn=in_all;
for i=0:1
    chip_addr = i;
    program_FPGA(sram_data_prtn,chip_addr,FPGA_lib,FPGA_xptr,'prtn','sram');
    %program_FPGA_trigger(FPGA_lib,FPGA_xptr);
    %DNAChip_program_chip_FPGA_sram_aug15(FPGA_lib,FPGA_xptr,in_all,chip_addr);
end

disp('Programming chips using FPGA complete')

elapsedtime=toc(tstart); % read elapsed time
disp(['SRAM programming took ' num2str(elapsedtime) ' seconds to run.'])

% set simulation values
% recommended clk freq: 5MHz
disp('Loading simulation parameters..')
pause(1);

%% Initial Conditions
prog_vect=repressilator_sr(prog_vect);
for i=0:9
    program_FPGA(prog_vect,i,FPGA_lib,FPGA_xptr,'prtn','sr');
end

disp('Initial condition set.')
pause(5);

%% reset FIFO for test
% Run this before adding new prog_vects
FIFO_reset(FPGA_xptr);

numRead = 10;%Jon edited to current value %40 % 20=1.92s. 200=19.2s
numOutputVar1 = 12; numOutputVar2 = 12;
%readarray = DNAChip_read_chip_FPGA_aug15(FPGA_lib,FPGA_xptr,numRead,numOutputVar1,numOutputVar2,ADCOUTtoCurrent_chip3,ADCOUTtoCurrent_chip2);
raw = read_chip_FPGA(FPGA_lib,FPGA_xptr,numRead,numOutputVar1,numOutputVar2);
for i=[1:12]
    readarray(:,i)=polyval(ADC_scale(:,i),raw(:,i));
end
assignin('base','readarray',readarray);
assignin('base','raw',raw);

%% Plot
%figure(12);
close all

figure
hold on
time=[1:size(readarray)]*0.002;
for i= [1,2,3]
    plot(time,readarray(:,i),'linewidth',1.5)
end
xlabel('Time (s)');
ylabel('Proteins per cell (nA)');
legend('Protein1','Protein2','Protein3','Location','Northwest')

figure
hold on
time=[1:size(readarray)]*0.002;
for i= [4,5,6]
    plot(time,readarray(:,i),'linewidth',1.5)
end
xlabel('Time (s)');
ylabel('mRNA per cell (nA)');
legend('mRNA1','mRNA2','mRNA3','Location','Northwest')

end

function in_all=repressilator_sram()
  load('sram.mat','in_all');
  assert(size(in_all,1) == 1024);
  assert(size(in_all,2) == 100);
end

function prog_vect=repressilator_sr(prog_vect)
for k = 1:4
    % Block 1
    prog_vect(1:7, k)=digital_current_to_closest_bits('IAtot_1',865,1); % IAtot = 865n
    prog_vect(8:14, k)=[0 0 0 0 0 0 0]; % IBtot = 0
    prog_vect(15:21, k)=digital_current_to_closest_bits('IKDfw_1',4,1); % IKDfw = 4n
    prog_vect(22:28, k)=digital_current_to_closest_bits('Ikr1_1',1000,1); % Ikr1 = 10n
    prog_vect(29:35, k)=digital_current_to_closest_bits('Ikr2_1',1,1); % Ikr2 = 1n
    prog_vect(36:42, k)=digital_current_to_closest_bits('IDfree_1',10,1); % IDfree = 10n
    prog_vect(43:49, k)=digital_current_to_closest_bits('IKDrv_1',10,1); % IKDrv = 10n
    prog_vect(50:56, k)=[0 0 0 0 0 0 0]; % IratC = 0

    % Block 2
    prog_vect(57:63, k)=digital_current_to_closest_bits('IAtot_2',0.865,1); % IAtot = 865p
    prog_vect(64:70, k)=digital_current_to_closest_bits('IBtot_2',10,1); % IBtot = 10n
    prog_vect(71:77, k)=digital_current_to_closest_bits('IKDfw_2',10,1); % IKDfw = 10n
    prog_vect(78:84, k)=digital_current_to_closest_bits('Ikr1_2',100,1); % Ikr1 = 10n
    prog_vect(85:91, k)=digital_current_to_closest_bits('Ikr2_2',1,1); % Ikr2 = 1n
    prog_vect(92:98, k)=digital_current_to_closest_bits('IDfree_2',10,1); % IDfree = 10n
    prog_vect(99:105, k)=digital_current_to_closest_bits('IKDrv_2',10,1); % IKDrv = 10n
    prog_vect(106:112, k)=[0 0 0 0 0 0 0]; % IratC = 0

    % Block 3
    prog_vect(113:119, k)=[0 0 0 0 0 0 0]; % IAtot = 0
    prog_vect(120:126, k)=digital_current_to_closest_bits('IBtot_3',10,1); % IBtot = 10n
    prog_vect(127:133, k)=digital_current_to_closest_bits('IKDfw_3',10,1); % IKDfw = 10n
    prog_vect(134:140, k)=digital_current_to_closest_bits('Ikr1_3',20,1); % Ikr1 = 20n
    prog_vect(141:147, k)=digital_current_to_closest_bits('Ikr2_3',1,1); % Ikr2 = 1n
    prog_vect(148:154, k)=digital_current_to_closest_bits('IDfree_3',10,1); % IDfree = 10n
    prog_vect(155:161, k)=digital_current_to_closest_bits('IKDrv_3',10,1); % IKDrv = 10n
    prog_vect(162:168, k)=[0 0 0 0 0 0 0]; % IratC = 0

    % Block 1
    prog_vect(288:291, k)=[0 1 0 1]; % hill_b<0:3> (1111: hill 1 ~ 0000: hill 4)
    prog_vect(292:293, k)=[1 0]; % A_FB_EN, B_FB_EN
    prog_vect(294:296, k)=[0 1 1]; % sel_useI, sel_Ctot, sel_rate
    % Block 2
    prog_vect(297:300, k)=[1 1 1 1]; % hill_b<0:3> (1111: hill 1 ~ 0000: hill 4)
    prog_vect(301:302, k)=[0 0]; % A_FB_EN, B_FB_EN
    prog_vect(303:305, k)=[0 1 1]; % sel_useI, sel_Ctot, sel_rate
    % Block 3
    prog_vect(306:309, k)=[1 1 1 1]; % hill_b<0:3> (1111: hill 1 ~ 0000: hill 4)
    prog_vect(310:311, k)=[0 0]; % A_FB_EN, B_FB_EN
    prog_vect(312:314, k)=[0 1 1]; % sel_useI, sel_Ctot, sel_rate
end
end
